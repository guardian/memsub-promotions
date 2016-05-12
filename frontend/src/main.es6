import "material-design-lite"
import 'babel-polyfill/dist/polyfill.min'

//views
import campaignList from "text!templates/Campaignlist.html"
import promotionList from "text!templates/PromotionList.html"
import promotionForm from "text!templates/PromotionForm.html"
import campaignForm from "text!templates/CampaignForm.html"
import chooseCampaign from "text!templates/ChooseCampaign.html"

// directives
import stageMenu from 'directives/StageMenu'
import mdlUpgrade from 'directives/MdlUpgrade'

// services
import RatePlanService from "services/RatePlanService"
import PromotionService from "services/PromotionService"
import CampaignService from "services/CampaignService"
import CountryService from "services/CountryService"

// controllers
import PromotionListController from "controllers/PromotionListController"
import EditPromotionController from "controllers/EditPromotionController"
import EditCampaignController from "controllers/EditCampaignController"
import StageController from "controllers/StageController"

let module = angular.module("Promotions", ['ui.router', 'ngCookies']);

module.service('promotionService', PromotionService)
      .service('campaignService', CampaignService)
      .service('ratePlanService', RatePlanService)
      .service('countryService', CountryService)
      .controller('promotionListController', PromotionListController)
      .controller('editPromotionController', EditPromotionController)
      .controller('editCampaignController', EditCampaignController)
      .controller('stageController', StageController)
      .directive('stageMenu', stageMenu)
      .directive('mdlUpgrade', mdlUpgrade)
;

module.config(($stateProvider, $urlRouterProvider) => {
    $urlRouterProvider.otherwise("/");
    $stateProvider
    .state('allPromotions', {
        template: campaignList,
        controller: 'promotionListController',
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
        template: chooseCampaign,
        url: "/"
    })
    .state('editPromotion', {
        url: "/promotion/edit/:uuid",
        template: promotionForm,
        controller: 'editPromotionController',
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