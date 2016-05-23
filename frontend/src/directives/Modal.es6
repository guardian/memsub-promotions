import template from "text!templates/Modal.html"


/* @ngInject */
export default () => {
    return {
        scope: {
            show: '=',
            title: '@title',
            product: '='
        },
        restrict: 'E',
        template: template,
        transclude: true
    };
}