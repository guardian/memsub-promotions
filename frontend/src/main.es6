//views
import table from "text!templates/Promotions.html"
import promotionForm from "text!templates/PromotionForm.html"
import campaignForm from "text!templates/CampaignForm.html"

// directives
import stageMenu from 'directives/StageMenu'
import mdlUpgrade from 'directives/MdlUpgrade'

// services
import RatePlanService from "services/RatePlanService"
import PromotionService from "services/PromotionService"
import CampaignService from "services/CampaignService"

// controllers
import PromotionListController from "controllers/PromotionListController"
import EditPromotionController from "controllers/EditPromotionController"
import EditCampaignController from "controllers/EditCampaignController"
import StageController from "controllers/StageController"

let module = angular.module("Promotions", ['ui.router', 'ngCookies']);

module.service('promotionService', PromotionService)
      .service('campaignService', CampaignService)
      .service('ratePlanService', RatePlanService)
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
            url: "/",
            template: table,
            controller: 'promotionListController',
            controllerAs: 'ctrl'
        })
        .state('singleCampaign', {
            url: "/campaign/:code",
            template: table,
            controller: 'promotionListController',
            controllerAs: 'ctrl'
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