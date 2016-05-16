import types from "text!templates/PromotionType.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            promotion: '='
        },
        restrict: 'E',
        template: types,
        controller: 'promotionTypeController',
        controllerAs: 'ctrl'
    };
}