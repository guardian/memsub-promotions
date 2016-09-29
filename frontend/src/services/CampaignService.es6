export default class {

    /* @ngInject */
    constructor($http, environmentService) {
        this.env = environmentService;
        this.$http = $http;
    }

    all() {
        return this.$http({
            method: 'GET',
            url: '/campaigns',
            params: {'group': this.env.getCampaignGroup()}
        }).then(response => response.data)
    }

    get(code) {
        return this.$http({
            method: 'GET',
            url: '/campaign',
            params: {'code': code}
        }).then(response => response.data)
    }

    save(campaign) {

        // Backwards compatibility of legacy serialisations where group was identified by product_family.
        // Deleting product_family tidies up old field upon saving
        campaign.group = campaign.group || campaign.product_family;
        delete campaign.product_family;

        return this.$http({
            method: 'POST',
            url: '/campaign',
            data: campaign
        }).then(response => response.data)
    }
}