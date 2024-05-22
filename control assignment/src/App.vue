<template>
  <div class="container">
    <div class="toolbar">
      <button class="btn" @click="addNode()">New node</button>
      <button class="btn" :class="{ active: connectActive }" @click="handleOperation('connect')">Connect</button>
      <button class="btn" :class="{ active: choosingLink || (editingGain && !connectActive) }" @click="handleOperation('edit')">Edit Gain</button>
      <button class="btn" :class="{ active: deleting }" @click="handleOperation('delete')">Delete</button>
      <button class="btn" :class="{ active: choosingNode=='input' }" @click="handleOperation('chooseinput')">Choose input</button>
      <button class="btn" :class="{ active: choosingNode=='output' }" @click="handleOperation('chooseoutput')">Choose output</button>
      <button class="btn" @click="solve()">Solve</button>
    </div>
    <Transition>
      <div class="toolbar" v-show="editingGain">
        <input type="text" placeholder="Enter gain" class="searchbar" v-model="enteredGain" @keyup.enter="enterGain">
        <button class="btn" @click="enterGain()">Enter</button>
      </div>
    </Transition>
    <p class="error" v-show="gainInputerror">Invalid input format. Enter a number.</p>
    <p class="error" v-show="solvingerror">Empty graph or you forgot to determine the input and output node </p>
    <div class="toolbar">
      <h4>Input node:</h4> <p>{{ inputNode }}</p> <h4>Output node:</h4> <p>{{ outputNode }}</p>
    </div>

    <div
      ref="stage"
      class="draw"
    ></div>
  </div >
  <div v-show="Openmodal">
  <solnmodal   @close="Openmodal = !Openmodal">
    <p>
     <h1> solution</h1>
      <div><h2>Gain: {{ Gain }}</h2></div>
      <div><h2>Forward paths:<div v-for="(row,rowindex) in ForwardPaths">P{{ rowindex+1 }}:<span v-for="col in row">{{ elements&&elements[col]? elements[col].attr().label.text : col }}</span>
          
        </div></h2>
        
      </div>
      <div><h2>Big Delta:{{ bigDelta }}</h2></div>
    <div><h2>Loops:<div v-for="(row,rowindex) in Loops">L{{ rowindex+1 }}:<span v-for="col in row">{{ elements&&elements[col]? elements[col].attr().label.text : col }}</span>
        </div></h2></div>
        <div><h2>Non Touching Loops:<div v-for="row in nonTouchingloops"><span v-for="col in row">L{{ elements&&elements[col]? elements[col].attr().label.text : col }},</span>
        </div></h2>

</div>
<div><h2>Deltas:<div v-for="(row,rowindex) in Deltas">
          <span>D{{ rowindex+1}}:</span>{{row }}
        </div></h2>

</div>
    </p>
   

    
  
  
  </solnmodal >
  </div>
</template>

<script>
import axios from 'axios';
import solnmodal from "./components/solnmodal.vue";
import multiLink from "./multiLink.js"
  export default {
  
    components: { solnmodal},
    
    data() {
      return {
        graph: null,
        paper: null,

        nodeNumber: 0,

        connecting: false,
        connectActive: false,
        firstNode: null,
        choosingLink: false,
        choosenLink: null,

        editingGain: false,
        enteredGain: null,
        gainInputerror: false,

        deleting: false,
        solvingerror:false,
        Openmodal:false,
        choosingNode: null, //null: not choosing, input: choosing input, output: choosing output
        inputNode: null,
        inputElement:null,
        outputNode: null,
        outputElement: null,
        elements: null,
        map: null,
        adjMatrix:null,
        nonTouchingloops:[[1,2],[1,3]],
        Loops:[[1,2],[2,3]],
        Gain:0,
        Deltas:[1,2,3,4,5],
        ForwardPaths:[[1,0],[1,2]],
        bigDelta:0,
      }
    },
    mounted() {
      let namespace = joint.shapes
      this.graph = new joint.dia.Graph({}, { cellNamespace: namespace })
      this.paper = new joint.dia.Paper({
          el: this.$refs.stage,
          model: this.graph,
          width: '90%',
          height: '80vh',
          gridSize: 1,
          cellViewNamespace: namespace,
          restrictTranslate: true
      })
      multiLink(this.graph, this.paper)

      this.paper.on('element:mouseover', (elementView)=> {
        if (this.connecting || this.deleting || this.choosingNode) {
          var currentElement = elementView.model
          currentElement.attr('body/stroke-width', '4')
          currentElement.attr('body/cursor', 'pointer')
          currentElement.attr('label/cursor', 'pointer')
        }
      })
      this.paper.on('element:mouseout', (elementView)=> {
        if (this.connecting || this.deleting || this.choosingNode) {
          let currentElement = elementView.model
          currentElement.attr('body/stroke-width', '2')
          currentElement.attr('body/cursor', 'move')
          currentElement.attr('label/cursor', 'move')
        }
      })
      this.paper.on('element:pointerclick', (elementView)=> {
        let currentElement = elementView.model
        if (this.connecting) {
          if (this.firstNode != null) {   //if the first node is selected already
            let link = new joint.shapes.standard.Link()
            link.source(this.firstNode)
            link.target(currentElement)
            link.attr('line/stroke-width', '2')
            if (this.firstNode.cid==currentElement.cid) {
              link.router('manhattan')
            }
            link.connector('rounded')
            this.choosenLink = link
            this.firstNode = null
            this.connecting = false
            this.editingGain = true
            currentElement.attr('body/stroke-width', '2')
            currentElement.attr('body/cursor', 'move')
            currentElement.attr('label/cursor', 'move')
          }
          else {   //if no node is selected yet
            this.firstNode = currentElement
          }
        }
        else if (this.deleting) {
          if (this.inputElement && currentElement.cid==this.inputElement.cid) {
            this.inputElement = null
            this.inputNode = null
          }
          if (this.outputElement && currentElement.cid==this.outputElement.cid) {
            this.outputElement = null
            this.outputNode = null
          }
          currentElement.remove()
          this.deleting = false
        }
        else if (this.choosingNode) {
          if (this.choosingNode=='input') {
            this.inputNode = currentElement.attr('label/text')
            this.inputElement = currentElement
          }
          else {
            this.outputNode = currentElement.attr('label/text')
            this.outputElement = currentElement
          }
          this.choosingNode = null
          currentElement.attr('body/stroke-width', '2')
        }
      })
      this.paper.on('link:mouseover', (linkView)=> {
        if (this.choosingLink || this.deleting) {
          let link = linkView.model
          link.attr('line/stroke-width', '4')
        }
      })
      this.paper.on('link:mouseout', (linkView)=> {
        if (this.choosingLink || this.deleting) {
          let link = linkView.model
          link.attr('line/stroke-width', '2')
        }
      })
      this.paper.on('link:pointerclick', (linkView)=> {
        let link = linkView.model
        if (this.choosingLink) {
          link.attr('line/stroke-width', '2')
          this.choosenLink = link
          this.choosingLink = false
          this.editingGain = true
        }
        else if (this.deleting) {
          link.remove()
          this.deleting = false
        }
      })
    },

    methods: {
      addNode() {
        let circle = new joint.shapes.standard.Circle()
        circle.translate(3,3)
        circle.resize(30, 30);
        circle.attr('label/text', this.nodeNumber++)
        circle.attr('body/fill', 'lightblue')
        circle.attr('body/stroke-width', '2')
        circle.addTo(this.graph)
        this.solvingerror=false
      },

      handleConnect() {
        this.connecting = !this.connecting
        this.connectActive = !this.connectActive
        this.firstNode = null
        this.enteredGain = null
        this.editingGain = false
      },

      handleEdit() {
        this.choosingLink = !this.choosingLink
        this.choosenLink = null
        this.enteredGain = null
        this.editingGain = false
        this.gainInputerror = false
      },

      enterGain() {
        if (isNaN(this.enteredGain)) {
          this.gainInputerror = true
          return
        }
        this.choosenLink.labels([{
            attrs: {
                text: {
                    text: this.enteredGain==null||this.enteredGain.trim()==''? 1 : this.enteredGain
                }
            }
        }])
        this.editingGain = false
        this.choosenLink.addTo(this.graph)
        this.choosenLink = null
        this.gainInputerror = false
        this.connectActive = false
      },

      handleDelete() {
        this.deleting = !this.deleting
      },

      chooseNode(type) {
        if (this.choosingNode==type) this.choosingNode = null
        else this.choosingNode = type
      },

      //keep only one operation running at a time
      handleOperation(operation) {
        if (this.connecting) {
          this.handleConnect()
          if (operation=='connect') return
        }
        else if (this.choosingLink) {
          this.handleEdit()
          if (operation=='edit') return
        }
        else if (this.editingGain) {
          this.editingGain = false
          this.choosenLink = null
          this.gainInputerror = false
          if ((this.connectActive && operation=='connect')) {
            this.connectActive = false
            return
          }
          this.connectActive = false
          if (!this.connectActive && operation=='gain') return
        }
        else if (this.deleting) {
          this.handleDelete()
          if (operation=='delete') return
        }
        else if (this.choosingNode != null) {
          let type = this.choosingNode
          this.choosingNode = null
          if (operation=='choose'+type) return
        }
        
        switch (operation) {
          case 'connect':
            this.handleConnect()
            break
          case 'edit': 
            this.handleEdit()
            break
          case 'delete':
            this.handleDelete()
            break
          case 'chooseinput':
            this.chooseNode('input')
            break
          case 'chooseoutput':
            this.chooseNode('output')
            break
          default:
            console.log('invalid operation code')
            break;
        }
      },

      createMatrix() {
        
        let elements = this.graph.getElements()
        
        // get the input and output nodes in position
        for (let i=0; i<elements.length; i++) {
          if (this.inputElement.cid==elements[i].cid) {
            let temp = elements[0]
            elements[0] = elements[i]
            elements[i] = temp
          }
          else if ((this.outputElement.cid==elements[i].cid)) {
            let temp = elements[elements.length-1]
            elements[elements.length-1] = elements[i]
            elements[i] = temp
          }       
        }
        // a map with key: shape id, value: shape index in elements array 
        let map = new Map()
        for (let i=0; i<elements.length; i++) {
          map.set(elements[i].cid, i)
        }
        // create the matrix
        let matrix = new Array(elements.length);
        for (let i = 0; i < elements.length; i++) {
            matrix[i] = new Array(elements.length).fill(0);
        }

        for (let link of this.graph.getLinks()) {
          let row = map.get(link.getSourceElement().cid)
          let column = map.get(link.getTargetElement().cid)
          matrix[row][column] += Number(link.label(0).attrs.text.text)
        }
        console.log(matrix)
        this.adjMatrix=matrix
        this.map = map
        this.elements = elements
      },
      checkValidity()
      {
        if(this.inputNode==null||this.outputNode==null||this.graph.getElements().length==0)this.solvingerror=true
        else this.solvingerror=false
      },async solve() {

        this.checkValidity()
        if(!this.solvingerror){
       this.createMatrix() 
    const baseUrl = 'http://localhost:8080';
    const url = baseUrl + '/sendGraph';
    console.log(this.adjMatrix)
    const data = {
      adjacencyMatrix: this.adjMatrix,
      source: this.map.get(this.inputElement.cid),
      des: this.map.get(this.outputElement.cid)
    };

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });

      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      // Fetch additional data from other endpoints if needed
      const forwardPathsResponse = await fetch(baseUrl + '/forwardPaths');
      const loopsResponse = await fetch(baseUrl + '/loops');
      const nonTouchingLoopsResponse = await fetch(baseUrl + '/nonTouchingLoops');
      const bigDeltaResponse = await fetch(baseUrl + '/bigDelta');
      const pathsDeltaResponse = await fetch(baseUrl + '/pathsDelta');
      const transferFunctionResponse = await fetch(baseUrl + '/transferFunction');

      this.ForwardPaths = await forwardPathsResponse.json();
      this.Loops = await loopsResponse.json();
      this.nonTouchingloops = await nonTouchingLoopsResponse.json();
      this.bigDelta = await bigDeltaResponse.json();
      this.Deltas = await pathsDeltaResponse.json();
      this.Gain = await transferFunctionResponse.json();

      // Do something with the fetched data if needed
      //console.log(forwardPaths, loops, nonTouchingLoops, bigDelta, pathsDelta, transferFunction);
    } catch (error) {
      console.error('There was a problem with your fetch operation:', error);
    }
    this.Openmodal=true
  }
  },
   
  }
  }
</script>

<style scoped>

html,
body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #363f4f;
}

.container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.toolbar {
  width: 60%;
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-top: 30px;
}

.searchbar {
  width: 80%;
  padding: 8px 16px;
  border-width: 1px;
  border-radius: 4px;
}

.error {
  color: red;
}

.v-enter-active {
  transition: all 0.5s ease-out 0.5s;
}

.v-leave-active {
  transition: all 0.5s ease-in;
}

.v-enter-from,
.v-leave-to {
  transform: translatey(-20px);
  opacity: 0;
}

.btn {
  background-color: #3498db; 
  color: #ffffff; 
  padding: 8px 16px; 
  border: none; 
  border-radius: 4px; 
  cursor: pointer;
  transition: background-color 0.3s ease; 
  user-select: none;
}

.btn:hover {
  background-color: #2980b9; 
}

.active {
  background-color: #2980b9; 
}

.draw {
  margin-top: 30px;
  width: 90%;
  height: 80vh;
  border: 1px solid black;
  border-radius: 5px;
  background-color: white;
  box-shadow: 5px 10px 10px 5px grey;
}
</style>
