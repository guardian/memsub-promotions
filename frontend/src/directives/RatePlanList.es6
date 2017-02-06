import template from "text!templates/RatePlanList.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            'productRatePlanIds': '=',
            'campaignGroup': '=',
            'discount': '=',
            'length': '='
        },
        restrict: 'E',
        template: template,
        controller: 'ratePlanListController',
        controllerAs: 'ctrl',

        link: (scope, elem, attrs, controller) => {
            scope.$watch('promotion', (n) => {
                controller.updateDiscount(n);
            });
            scope.$watch('promotion', (n) => {
                controller.updateLength(n);
            });
            scope.$watch('productRatePlanIds', (n) => {
                if (n) {
                    controller.populateRatePlans(n)
                }
            })
        }
    };
}