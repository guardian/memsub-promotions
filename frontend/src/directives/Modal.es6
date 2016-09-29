import template from "text!templates/Modal.html"


/* @ngInject */
export default () => {
    return {
        scope: {
            show: '=',
            title: '@title',
            campaignGroup: '='
        },
        restrict: 'E',
        template: template,
        transclude: true
    };
}