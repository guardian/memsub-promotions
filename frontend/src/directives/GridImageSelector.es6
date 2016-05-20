import template from "text!templates/GridImageSelector.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            'landingPage': '='
        },
        restrict: 'E',
        template: template,
        link: (scope, elem, attrs) => {
            window.addEventListener('message', (event) => {
                scope.$apply(function(s) {
                    let assets = event.data.crop.data.assets;
                    assets.sort((b, a) => (a.dimensions.width * a.dimensions.height) - (b.dimensions.width * b.dimensions.height));
                    s.landingPage.imageUrl = assets[0].file;
                    s.show = false;
                });
            }, false);
        }
    };
}