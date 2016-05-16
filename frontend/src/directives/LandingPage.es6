import template from "text!templates/LandingPage.html"


/* @ngInject */
export default () => {
    return {
        scope: {
            landingPage: '='
        },
        restrict: 'E',
        template: template
    };
}