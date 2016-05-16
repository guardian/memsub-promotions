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
        controllerAs: 'ctrl',
        link: (scope, elem, attrs, controller) => {
            scope.$watch('promotion', (n) => {
                if (n) {
                    controller.updateSelectedTab(n.promotionType.name)
                }
            })
        }
    };
}