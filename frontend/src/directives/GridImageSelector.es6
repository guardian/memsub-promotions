import template from "text!templates/GridImageSelector.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            'landingPage': '='
        },
        restrict: 'E',
        template: template,
        controller: 'gridImageSelectorController',
        controllerAs: 'ctrl',
        link: (scope, elem, attrs, ctrl) => {
            
            scope.$watch('landingPage.imageUrl', function(newValue) {
                ctrl.imageUrlChanged(newValue);
            });
            
            window.addEventListener('message', (event) => {
                scope.$apply(function() {
                    let assets = event.data.crop.data.assets;
                    ctrl.imageSelected(assets, event.origin);
                });
            }, false);
        }
    };
}