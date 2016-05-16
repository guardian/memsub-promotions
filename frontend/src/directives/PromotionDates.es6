import template from "text!templates/PromotionDates.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            'promotion': '='
        },
        restrict: 'E',
        template: template,
        controller: 'promotionDatesController',
        controllerAs: 'ctrl'
    };
}