import menu from "text!templates/StageMenu.html"

/* @ngInject */
export default () => {
    return {
        scope: true,
        restrict: 'A',
        template: menu,
        replace: true,
        controller: 'stageController',
        controllerAs: 'ctrl'
    };
}