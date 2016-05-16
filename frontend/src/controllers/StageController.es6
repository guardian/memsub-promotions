const STAGE = 'stage';

export default class {

    /* @ngInject */
    constructor($state, $rootScope, $scope, $cookies) {
        this.$state = $state;
        let cookieValue = $cookies.get(STAGE);
        $rootScope.stage = {name: cookieValue ? cookieValue : "PROD"};
        this.$cookies = $cookies;
        this.$scope = $scope;
    }

    setStage(stage) {
        this.$scope.stage.name = stage;
        this.$cookies.put(STAGE, stage);
        this.$state.go(this.$state.current, {}, {reload: true});
    }
}
