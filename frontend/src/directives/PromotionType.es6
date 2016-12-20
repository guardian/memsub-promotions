import types from "text!templates/PromotionType.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            promotionType: '=',
            campaignGroup: '=',
            coPromotionType: '='
        },
        restrict: 'E',
        template: types,
        controller: 'promotionTypeController',
        controllerAs: 'ctrl',
        link: (scope, elem, attrs, controller) => {
            scope.$watch('promotionType', (n) => {
                if (n) {
                    controller.updateSelectedTab(n.name)
                }
            })
        }
    };
}