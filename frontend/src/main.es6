import 'babel-polyfill/dist/polyfill.min'

import 'angular'
import 'angular-ui-router'
import 'angular-cookies'
import 'angular-material'
import 'angular-messages'
import 'angular-uuid'

//views
import campaignList from "text!templates/CampaignList.html"
import promotionList from "text!templates/PromotionList.html"
import promotionForm from "text!templates/PromotionForm.html"
import campaignForm from "text!templates/CampaignForm.html"

// directives
import environmentMenu from 'directives/EnvironmentMenu'
import channelCodes from 'directives/ChannelCodes'
import promotionType from 'directives/PromotionType'
import multiPromotionType from 'directives/MultiPromotionType'
import deleteEmpty from 'directives/DeleteEmpty'
import promotionDates from 'directives/PromotionDates'
import landingPage from 'directives/LandingPage'
import ratePlanList from 'directives/RatePlanList'
import availableCountries from 'directives/AvailableCountries'
import modal from 'directives/Modal'
import previewPromotion from 'directives/PreviewPromotion'
import gridImageSelector from 'directives/GridImageSelector'

//config
import dateConfig from 'config/DateConfig'
import urlConfig from 'config/UrlConfig'

// services
import RatePlanService from "services/RatePlanService"
import PromotionService from "services/PromotionService"
import CampaignService from "services/CampaignService"
import CountryService from "services/CountryService"
import EnvironmentService from "services/EnvironmentService"

// controllers
import PromotionListController from "controllers/PromotionListController"
import CampaignListController from "controllers/CampaignListController"
import PromotionFormController from "controllers/PromotionFormController"
import EditCampaignController from "controllers/EditCampaignController"
import ChannelCodesController from "controllers/ChannelCodesController"
import PromotionDatesController from "controllers/PromotionDatesController"
import PromotionTypeController from "controllers/PromotionTypeController"
import MultiPromotionTypeController from "controllers/MultiPromotionTypeController"
import RatePlanListController from "controllers/RatePlanListController"
import AvailableCountriesController from "controllers/AvailableCountriesController"
import GridImageSelectorController from "controllers/GridImageSelectorController"
import EnvironmentController from "controllers/EnvironmentController"
import PreviewPromotionController from "controllers/PreviewPromotionController"
import LandingPageController from "controllers/LandingPageController"
import '../scss/main.scss';

let module = angular.module("Promotions", ['ui.router', 'ngMessages', 'ngCookies', 'ngMaterial', 'angular-uuid']);

module.service('promotionService', PromotionService)
      .service('campaignService', CampaignService)
      .service('ratePlanService', RatePlanService)
      .service('countryService', CountryService)
      .service('environmentService', EnvironmentService)
      .controller('promotionListController', PromotionListController)
      .controller('campaignListController', CampaignListController)
      .controller('promotionFormController', PromotionFormController)
      .controller('editCampaignController', EditCampaignController)
      .controller('channelCodesController', ChannelCodesController)
      .controller('promotionTypeController', PromotionTypeController)
      .controller('multiPromotionTypeController', MultiPromotionTypeController)
      .controller('promotionDatesController', PromotionDatesController)
      .controller('environmentController', EnvironmentController)
      .controller('ratePlanListController', RatePlanListController)
      .controller('availableCountriesController', AvailableCountriesController)
      .controller('previewPromotionController', PreviewPromotionController)
      .controller('gridImageSelectorController', GridImageSelectorController)
      .controller('landingPageController', LandingPageController)
      .directive('promotionType', promotionType)
      .directive('multiPromotionType', multiPromotionType)
      .directive('landingPage', landingPage)
      .directive('environmentMenu', environmentMenu)
      .directive('channelCodes', channelCodes)
      .directive('deleteEmpty', deleteEmpty)
      .directive('modal', modal)
      .directive('promotionDates', promotionDates)
      .directive('ratePlanList', ratePlanList)
      .directive('availableCountries', availableCountries)
      .directive('gridImageSelector', gridImageSelector)
      .directive('previewPromotion', previewPromotion)
      .config(dateConfig)
      .config(urlConfig)
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
        url: "/campaign/promotions/:code",
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
        url: "/campaign/:code",
        template: campaignForm,
        controller: 'editCampaignController',
        controllerAs: 'ctrl'
    });
});

module.run(($rootScope, $state, $stateParams) => {
    $rootScope.$stateParams = $stateParams;
});
