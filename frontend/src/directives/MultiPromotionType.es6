import types from "text!templates/MultiPromotionType.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            promotion: '=',
            product: '='
        },
        restrict: 'E',
        template: types,
        controller: 'multiPromotionTypeController',
        controllerAs: 'ctrl'
    };
}