import menu from "text!templates/EnvironmentMenu.html"

/* @ngInject */
export default () => {
    return {
        scope: true,
        restrict: 'A',
        template: menu,
        replace: true,
        controller: 'environmentController',
        controllerAs: 'ctrl'
    };
}