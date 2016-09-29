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

        // Backwards compatibility of legacy serialisations where group was identified by productFamily.
        // Deleting productFamily tidies up old field upon saving
        campaign.group = campaign.group || campaign.productFamily;
        delete campaign.productFamily;

        return this.$http({
            method: 'POST',
            url: '/campaign',
            data: campaign
        }).then(response => response.data)
    }
}