function adjustVertices(graph, cell) {
    cell = cell.model || cell;

    if (cell instanceof joint.dia.Element) {
       
        _.chain(graph.getConnectedLinks(cell))
            .groupBy(function(link) {
                return _.omit([link.source().id, link.target().id], cell.id)[0];
            })
            .each(function(group, key) {
                if (key !== 'undefined') adjustVertices(graph, _.first(group));
            })
            .value();

        return;
    }

    var sourceId = cell.get('source').id || cell.previous('source').id;
    var targetId = cell.get('target').id || cell.previous('target').id;

    if (!sourceId || !targetId) {
        cell.unset('vertices');
        return;
    }

    if (sourceId==targetId) return

    var siblings = graph.getLinks().filter(function(sibling) {

        var siblingSourceId = sibling.source().id;
        var siblingTargetId = sibling.target().id;

        return ((siblingSourceId === sourceId) && (siblingTargetId === targetId))
            || ((siblingSourceId === targetId) && (siblingTargetId === sourceId));
    });

    var numSiblings = siblings.length;
    switch (numSiblings) {

        case 0:
        case 1: {
            break;
        }
        default: {

            var sourceCenter = graph.getCell(sourceId).getBBox().center();
            var targetCenter = graph.getCell(targetId).getBBox().center();
            var midPoint = g.Line(sourceCenter, targetCenter).midpoint();

            var theta = sourceCenter.theta(targetCenter);

            var GAP = 20;

            _.each(siblings, function(sibling, index) {
                var offset = GAP * Math.ceil(index / 2);

                var sign = ((index % 2) ? 1 : -1);

                if ((numSiblings % 2) === 0) {
                    offset -= ((GAP / 2) * sign);
                }

                var reverse = ((theta < 180) ? 1 : -1);

                var angle = g.toRad(theta + (sign * reverse * 90));
                var vertex = g.Point.fromPolar(offset, angle, midPoint).toJSON();

                sibling.vertices([vertex]);
            });
        }
    }
}

function bindInteractionEvents(graph, paper) {

    var adjustGraphVertices = _.partial(adjustVertices, graph);

    graph.on('add remove change:source change:target', adjustGraphVertices);

    paper.on('cell:pointerup', adjustGraphVertices);
}

export default bindInteractionEvents