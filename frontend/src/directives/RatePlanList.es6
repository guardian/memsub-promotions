import template from "text!templates/RatePlanList.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            'productRatePlanIds': '=',
            'product': '='
        },
        restrict: 'E',
        template: template,
        controller: 'ratePlanListController',
        controllerAs: 'ctrl',
        link: (scope, elem, attrs, controller) => {
            scope.$watch('productRatePlanIds', (n) => {
                if (n) {
                    controller.populateRatePlans(n)
                }
            })
        }
    };
}