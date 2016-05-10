
// directives
import promotionList from 'directives/PromotionList'

// services
import PromotionService from "services/PromotionService"

// controllers
import PromotionListController from "controllers/PromotionListController"


angular.module("Promotions", [])
       .service('promotionService', PromotionService)
       .controller('promotionListController', PromotionListController)
       .directive('promotionList', promotionList);
