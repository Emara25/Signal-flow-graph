<template>
    <transition name="fade">
    <div class="vue-modal">
        <transition name="drop-in">
          <div class="veu-modal-inner">
            <div class="vue-modal-content">
              <slot />
              <button type="button" @click="$emit('close')">Close</button>
            </div>
          </div>
        </transition>
      </div>
      </transition>
  </template>
<script>
export default {
 
};
</script>
<style scoped>
.drop-in-enter-active,
.drop-in-leave-active {
  transition: all 0.3s ease-out;
}

.drop-in-enter-from,
.drop-in-leave-to {
  opacity: 0;
  transform: translate(0, -50px);
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}
::before,
::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
.vue-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.vue-modal-inner {
  max-width: 500px;
  margin: 2rem auto;
}
.vue-modal-content {
  position: relative;
  background-color: #fff;
  border: 1px solid rgba(0, 0, 0, 0.3);
  background-clip: padding-box;
  border-radius: 0.3rem;
  padding: 1rem;
}

</style>