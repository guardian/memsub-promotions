const STAGE = 'stage';

export default class {

    /* @ngInject */
    constructor($state, $rootScope, $scope, $cookies) {
        this.$state = $state;
        $rootScope.stage = {name: "PROD"};
        this.$scope = $scope;
        this.$cookies = $cookies;
        this.$scope.stage.name = this.$cookies.get(STAGE);
    }

    setStage(stage) {
        this.$scope.stage.name = stage;
        this.$cookies.put(STAGE, stage);
        this.$state.go('allPromotions', {}, {reload: true});
    }
}
