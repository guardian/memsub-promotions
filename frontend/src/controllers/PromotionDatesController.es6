export default class {

    /* @ngInject */
    constructor($scope) {
        this.$scope = $scope;
    }

    addExpiryDate() {
        this.$scope.promotion.expires = new Date();
    }
}
