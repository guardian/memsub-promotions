import template from "text!templates/Modal.html"


/* @ngInject */
export default () => {
    return {
        scope: {
            show: '=show',
            title: '@title',
            promotion: '=promotion'
        },
        restrict: 'E',
        template: template,
        transclude: true
    };
}