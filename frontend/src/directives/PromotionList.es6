import table from "text!templates/Promotions.html"

/* @ngInject */
export default () => {
    return {
        scope: true,
        restrict: 'A',
        template: table,
        controller: 'promotionListController',
        controllerAs: 'ctrl'
    };
}