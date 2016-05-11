//views
import table from "text!templates/Promotions.html"
import form from "text!templates/Form.html"

// directives
import stageMenu from 'directives/StageMenu'
import mdlUpgrade from 'directives/MdlUpgrade'

// services
import RatePlanService from "services/RatePlanService"
import PromotionService from "services/PromotionService"

// controllers
import PromotionListController from "controllers/PromotionListController"
import EditPromotionController from "controllers/EditPromotionController"
import StageController from "controllers/StageController"

let module = angular.module("Promotions", ['ui.router', 'ngCookies']);

module.service('promotionService', PromotionService)
      .service('ratePlanService', RatePlanService)
      .controller('promotionListController', PromotionListController)
      .controller('editPromotionController', EditPromotionController)
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
           url: "/:code",
           template: table,
           controller: 'promotionListController',
           controllerAs: 'ctrl'
       })
       .state('editPromotion', {
           url: "/edit/:uuid",
           template: form,
           controller: 'editPromotionController',
           controllerAs: 'ctrl'
       });
});

module.run(($rootScope, $state, $stateParams) => {
    $rootScope.$stateParams = $stateParams;
});