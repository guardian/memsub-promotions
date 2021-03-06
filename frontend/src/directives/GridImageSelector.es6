import template from "text!templates/GridImageSelector.html"

/* @ngInject */
export default ($timeout) => {
    return {
        scope: {
            'image': '=',
            'label': '@',
            'change': '&'
        },
        restrict: 'E',
        template: template,
        controller: 'gridImageSelectorController',
        controllerAs: 'ctrl',
        link: (scope, elem, attrs, ctrl) => {

            let iframe = elem[0].querySelector('iframe');
            scope.$watch('image', function(newValue) {
                ctrl.imageChanged(newValue);
            });
            
            window.addEventListener('message', (event) => {
                if(iframe.contentWindow == event.source) {
                    scope.$apply(function() {
                        ctrl.imageSelected(event.data.image.data, event.data.crop.data, event.origin);
                        if (typeof scope.change === 'function') {
                            $timeout(scope.change, 0);
                        }
                    });
                }
            }, false);
        }
    };
}