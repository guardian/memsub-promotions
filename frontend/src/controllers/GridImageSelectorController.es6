export default class {

    /* @ngInject */
    constructor($scope, environmentService) {
        this.gridOrigin = 'https://' + environmentService.getGridUrl();
        $scope.gridUrl = this.gridOrigin;

        $scope.product = environmentService.getProduct();
        this.environmentService = environmentService;
        this.$scope = $scope;
    }

    imageUrlChanged(newUrl) {
        
        if (!newUrl) {
            return;
        }

        let matched = newUrl.match(/\/([A-Za-z0-9]+)\/.*$/);

        if (typeof matched[1] != 'undefined') {
            this.$scope.gridUrl = this.gridOrigin+ '/images/' + matched[1];
        }
    }

    imageSelected(images, origin) {
        if (origin != this.gridOrigin) {
            return;
        }

        images.sort((b, a) => (a.dimensions.width * a.dimensions.height) - (b.dimensions.width * b.dimensions.height));
        this.$scope.landingPage.imageUrl = images[0].file;
        this.$scope.show = false;
    }
}
