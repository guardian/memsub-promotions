import template from "text!templates/LandingPage.html"


/* @ngInject */
export default () => {
    return {
        scope: {
            promotion: '='
        },
        restrict: 'E',
        template: template,
        controller: 'landingPageController',
        controllerAs: 'ctrl'
    };
}