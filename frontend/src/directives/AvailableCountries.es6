import template from "text!templates/AvailableCountries.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            'countries': '='
        },
        restrict: 'E',
        template: template,
        controller: 'availableCountriesController',
        controllerAs: 'ctrl',
        link: (scope, elem, attrs, controller) => {
            scope.$watch('countries', (n) => {
                if (n) {
                    controller.populateCountryGroups(n)
                }
            })
        }
    };
}