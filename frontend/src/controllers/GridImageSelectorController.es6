export default class {

    /* @ngInject */
    constructor($scope, environmentService) {
        this.gridOrigin = 'https://' + environmentService.getGridUrl();
        $scope.gridUrl = this.gridOrigin;

        $scope.product = environmentService.getProduct();
        this.environmentService = environmentService;
        this.$scope = $scope;
    }

    generateSrcSet(availableImages) {
        this.$scope.srcset = availableImages.map(img => img.path + " " + img.width + "w").join(", ")
    }

    imageChanged(newImage) {
        
        if (!newImage || !newImage.availableImages.length) {
            return;
        }

        this.generateSrcSet(newImage.availableImages);
        let matched = newImage.availableImages[0].path.match(/\/([A-Za-z0-9]+)\/.*$/);

        if (matched && typeof matched[1] != 'undefined') {
            this.$scope.gridUrl = this.gridOrigin+ '/images/' + matched[1];
        }
    }

    imageSelected(image, crop, origin) {
        if (origin != this.gridOrigin) {
            return;
        }
        
        this.$scope.image = {
            metadata: image.metadata,
            altText: image.metadata.description,
            availableImages: crop.assets.map(asset => ({path: asset.secureUrl, width: asset.dimensions.width}))
        };

        let sorted = crop.assets.sort((b, a) => a.dimensions.width - b.dimensions.width);
        this.$scope.url = sorted[0].secureUrl;
        this.$scope.show = false;
    }
}
