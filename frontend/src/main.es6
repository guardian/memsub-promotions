import 'babel-polyfill/dist/polyfill.min'

import 'angular'
import 'angular-ui-router'
import 'angular-cookies'
import 'angular-material'
import 'angular-uuid'

//views
import campaignList from "text!templates/CampaignList.html"
import promotionList from "text!templates/PromotionList.html"
import promotionForm from "text!templates/PromotionForm.html"
import campaignForm from "text!templates/CampaignForm.html"

// directives
import stageMenu from 'directives/StageMenu'
import channelCodes from 'directives/ChannelCodes'
import promotionType from 'directives/PromotionType'
import deleteEmpty from 'directives/DeleteEmpty'
import promotionDates from 'directives/PromotionDates'
import landingPage from 'directives/LandingPage'
import ratePlanList from 'directives/RatePlanList'
import availableCountries from 'directives/AvailableCountries'
import modal from 'directives/Modal'

//config
import dateConfig from 'config/DateConfig'

// services
import RatePlanService from "services/RatePlanService"
import PromotionService from "services/PromotionService"
import CampaignService from "services/CampaignService"
import CountryService from "services/CountryService"

// controllers
import PromotionListController from "controllers/PromotionListController"
import CampaignListController from "controllers/CampaignListController"
import PromotionFormController from "controllers/PromotionFormController"
import EditCampaignController from "controllers/EditCampaignController"
import ChannelCodesController from "controllers/ChannelCodesController"
import PromotionDatesController from "controllers/PromotionDatesController"
import PromotionTypeController from "controllers/PromotionTypeController"
import RatePlanListController from "controllers/RatePlanListController"
import AvailableCountriesController from "controllers/AvailableCountriesController"
import StageController from "controllers/StageController"

let module = angular.module("Promotions", ['ui.router', 'ngCookies', 'ngMaterial', 'angular-uuid']);

module.service('promotionService', PromotionService)
      .service('campaignService', CampaignService)
      .service('ratePlanService', RatePlanService)
      .service('countryService', CountryService)
      .controller('promotionListController', PromotionListController)
      .controller('campaignListController', CampaignListController)
      .controller('promotionFormController', PromotionFormController)
      .controller('editCampaignController', EditCampaignController)
      .controller('channelCodesController', ChannelCodesController)
      .controller('promotionTypeController', PromotionTypeController)
      .controller('promotionDatesController', PromotionDatesController)
      .controller('stageController', StageController)
      .controller('ratePlanListController', RatePlanListController)
      .controller('availableCountriesController', AvailableCountriesController)
      .directive('promotionType', promotionType)
      .directive('landingPage', landingPage)
      .directive('stageMenu', stageMenu)
      .directive('channelCodes', channelCodes)
      .directive('deleteEmpty', deleteEmpty)
      .directive('modal', modal)
      .directive('promotionDates', promotionDates)
      .directive('ratePlanList', ratePlanList)
      .directive('availableCountries', availableCountries)
      .config(dateConfig)
;

module.config(($stateProvider, $urlRouterProvider) => {
    $urlRouterProvider.otherwise("/");
    $stateProvider
    .state('allPromotions', {
        template: campaignList,
        controller: 'campaignListController',
        controllerAs: 'ctrl',
        abstract: true
    })
    .state('allPromotions.singleCampaign', {
        url: "/campaign/:code",
        template: promotionList,
        controller: 'promotionListController',
        controllerAs: 'ctrl'
    })
    .state('allPromotions.chooseCampaign', {
        template: '',
        url: "/"
    })
    .state('promotion', {
        url: "/promotion/:uuid",
        template: promotionForm,
        controller: 'promotionFormController',
        controllerAs: 'ctrl'
    })
    .state('createPromotion', {
        url: "/promotion/new/:campaignCode",
        template: promotionForm,
        controller: 'promotionFormController',
        controllerAs: 'ctrl'
    })
    .state('editCampaign', {
        url: "/campaign/edit/:code",
        template: campaignForm,
        controller: 'editCampaignController',
        controllerAs: 'ctrl'
    });
});


module.run(($rootScope, $state, $stateParams) => {
    $rootScope.$stateParams = $stateParams;
});