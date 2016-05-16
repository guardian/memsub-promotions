export default class {

    /* @ngInject */
    constructor($scope, countryService) {
        this.$scope = $scope;
        this.$scope.countryGroups = {};
        this.countryGroups = countryService.all();
        this.countryGroups.then(countries => $scope.countryGroups = countries);
    }

    arrayToMap(array) {
        return array.reduce((obj, id) => {
            let newObject = {};
            newObject[id] = true;
            return Object.assign(obj, newObject);
        }, {});
    }

    populateCountryGroups(countries) {
        this.countryGroups.then(countryGroups => {
            this.$scope.selectedCountryGroups = this.arrayToMap(countries.map(countryCode =>
                countryGroups.filter(cg => cg.countries.includes(countryCode))[0]).map(cg => cg.id));
        });
    }

    applyCountryGroups(countryGroupIds) {
        countryGroupIds = Object.keys(countryGroupIds).filter(id => countryGroupIds[id] == true);
        let selectedCountries = this.countryGroups.then(countryGroups => countryGroups
            .filter(group => countryGroupIds.includes(group.id))
            .map(group => group.countries)
            .reduce((arr, countries) => arr.concat(countries), [])
        );
        selectedCountries.then(countries => this.$scope.countries = countries);
    }

}
