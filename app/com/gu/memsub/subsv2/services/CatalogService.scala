package com.gu.memsub.subsv2.services

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.gu.aws.AwsS3
import com.gu.memsub.BillingPeriod._
import com.gu.memsub.Subscription.ProductRatePlanId
import com.gu.memsub._
import com.gu.memsub.subsv2.CatalogPlan._
import com.gu.memsub.subsv2._
import com.gu.memsub.subsv2.reads.CatJsonReads._
import com.gu.memsub.subsv2.reads.CatPlanReads
import com.gu.memsub.subsv2.reads.CatPlanReads._
import com.gu.memsub.subsv2.reads.ChargeListReads.{ProductIds, _}
import play.api.libs.json.{Reads => JsReads, _}
import scalaz.Validation.FlatMap._
import scalaz.syntax.monad._
import scalaz.syntax.nel._
import scalaz.syntax.std.either._
import scalaz.{EitherT, Monad, NonEmptyList, Validation, ValidationNel, \/}
import scalaz.syntax.apply.ToApplyOps

object FetchCatalog {
  def fromS3[M[_] : Monad](zuoraEnvironment: String, s3Client: AmazonS3 = AwsS3.client): M[String \/ JsValue] = {
    val catalogRequest = new GetObjectRequest("gu-zuora-catalog", s"PROD/Zuora-$zuoraEnvironment/catalog.json")
    AwsS3.fetchJson(s3Client, catalogRequest).point[M]
  }

}

class CatalogService[M[_] : Monad](productIds: ProductIds, fetchCatalog: M[String \/ JsValue], unsafeGet: M[Catalog] => Catalog, stage: String) {

  case class ErrorReport(problem: String, underlying: Map[ProductRatePlanId, String]) {
    override def toString = s"\n$problem\n\n--- HERE IS WHAT WE TRIED --\n" +
      underlying.foldLeft("") { case (a, (id, str)) => s"$a${id.get}: $str\n" }
  }

  /*
  frontend id is to prioritise searching for tagged rateplans so we can look for 6 for 6 or quarterly and find the tagged one, but since most of the others won't
  be tagged, we need to fall back for untagged ones.
  in time, I expect them all to be requested by the tag, so we can make frontend id non optional
  in time, I also expect they will all be tagged in zuora, so we don't need to then try untagged ones
   */
  def one[P <: CatalogPlan[Product, ChargeList, Status]](plans: List[CatalogZuoraPlan], name: String, frontendId: FrontendId)(implicit p: CatPlanReads[P]): Validation[NonEmptyList[String], P] = {
    many(plans.filter(_.frontendId.contains(frontendId)), name).flatMap {
      case a if a.size == 1 => Validation.s(a.head)
      case e => Validation.failureNel(s"Too many plans! $e")
    }
  }

  def many[P <: CatalogPlan[Product, ChargeList, Status]](plans: List[CatalogZuoraPlan], name: String)(implicit p: CatPlanReads[P]): ValidationNel[String, NonEmptyList[P]] = {
    val parsed: List[(ProductRatePlanId, ValidationNel[String, P])] =
      plans.map(plan => (plan.id, p.read(productIds, plan)))

    val failures = parsed.filter { case (_, validation) => validation.isFailure }
      .map { case (id, err) => (id, err.swap.map(_.list.toList).getOrElse(List.empty).mkString(", ")) }
      .toMap

    parsed.map { case (_, validation) => validation }
      .flatMap(_.toList) match {
      case n :: ns => Validation.success[NonEmptyList[String], NonEmptyList[P]](NonEmptyList.fromSeq(n, ns))
      case Nil => Validation.failureNel(ErrorReport(s"Failed to find $name in $stage", failures).toString)
    }
  }

  def joinUp: M[String \/ List[CatalogZuoraPlan]] = (for {
    catalog <- EitherT[String, M, JsValue](fetchCatalog)
    catalogPlans <- EitherT[String, M, List[CatalogZuoraPlan]](Json.fromJson[List[CatalogZuoraPlan]](catalog).asEither.toDisjunction.leftMap(_.toString).point[M])
  } yield catalogPlans).run

  def validatePlans(plans: List[CatalogZuoraPlan]): Validation[NonEmptyList[String], Catalog] = for {
    digipack <- (
      one[Digipack[Month.type]](plans, "Digipack month", FrontendId.Monthly) |@|
        one[Digipack[Quarter.type]](plans, "Digipack quarter", FrontendId.Quarterly) |@|
        one[Digipack[Year.type]](plans, "Digipack year", FrontendId.Yearly)
      ) (DigipackPlans)
    supporterPlus <- (
      one[SupporterPlus[Month.type]](plans, "Supporter Plus month", FrontendId.Monthly) |@|
        one[SupporterPlus[Year.type]](plans, "Supporter Plus year", FrontendId.Yearly)
      ) (SupporterPlusPlans)
    tierThree <- (
      one[TierThree[Month.type]](plans, "Supporter Plus & Guardian Weekly Domestic - Monthly", FrontendId.TierThreeMonthlyDomestic) |@|
        one[TierThree[Year.type]](plans, "Supporter Plus & Guardian Weekly Domestic - Annual", FrontendId.TierThreeAnnualDomestic) |@|
        one[TierThree[Month.type]](plans, "Supporter Plus & Guardian Weekly ROW - Monthly", FrontendId.TierThreeMonthlyROW) |@|
        one[TierThree[Year.type]](plans, "Supporter Plus & Guardian Weekly ROW - Annual", FrontendId.TierThreeAnnualROW) |@|
        one[TierThree[Month.type]](plans, "Supporter Plus, Guardian Weekly Domestic & Archive - Monthly", FrontendId.TierThreeMonthlyDomesticV2) |@|
        one[TierThree[Year.type]](plans, "Supporter Plus, Guardian Weekly Domestic & Archive - Annual", FrontendId.TierThreeAnnualDomesticV2) |@|
        one[TierThree[Month.type]](plans, "Supporter Plus, Guardian Weekly ROW & Archive - Monthly", FrontendId.TierThreeMonthlyROWV2) |@|
        one[TierThree[Year.type]](plans, "Supporter Plus, Guardian Weekly ROW & Archive - Annual", FrontendId.TierThreeAnnualROWV2)
      ) (TierThreePlans)
    contributor <- one[Contributor](plans, "Contributor month", FrontendId.Monthly)
    voucher <- many[Voucher](plans, "Paper voucher")
    digitalVoucher <- many[DigitalVoucher](plans, "Paper digital voucher")
    delivery <- many[Delivery](plans, "Paper delivery")
    nationalDelivery <- many[NationalDelivery](plans, "Paper - National Delivery")
    weeklyZoneA <- (
      one[WeeklyZoneA[SixWeeks.type]](plans, "Weekly Zone A Six weeks", FrontendId.SixWeeks) |@|
        one[WeeklyZoneA[Quarter.type]](plans, "Weekly Zone A quarter", FrontendId.Quarterly) |@|
        one[WeeklyZoneA[Year.type]](plans, "Weekly Zone A year", FrontendId.Yearly) |@|
        one[WeeklyZoneA[OneYear.type]](plans, "Weekly Zone A one year", FrontendId.OneYear)
      ) (WeeklyZoneAPlans)
    weeklyZoneB <- (
      one[WeeklyZoneB[Quarter.type]](plans, "Weekly Zone B quarter", FrontendId.Quarterly) |@|
        one[WeeklyZoneB[Year.type]](plans, "Weekly Zone B year", FrontendId.Yearly) |@|
        one[WeeklyZoneB[OneYear.type]](plans, "Weekly Zone B one year", FrontendId.OneYear)
      ) (WeeklyZoneBPlans)
    weeklyZoneC <- (
      one[WeeklyZoneC[SixWeeks.type]](plans, "Weekly Zone C Six weeks", FrontendId.SixWeeks) |@|
        one[WeeklyZoneC[Quarter.type]](plans, "Weekly Zone C quarter", FrontendId.Quarterly) |@|
        one[WeeklyZoneC[Year.type]](plans, "Weekly Zone C year", FrontendId.Yearly)
      ) (WeeklyZoneCPlans)
    weeklyDomestic <- (
      one[WeeklyDomestic[SixWeeks.type]](plans, "Weekly Domestic Six weeks", FrontendId.SixWeeks) |@|
        one[WeeklyDomestic[Quarter.type]](plans, "Weekly Domestic quarter", FrontendId.Quarterly) |@|
        one[WeeklyDomestic[Year.type]](plans, "Weekly Domestic year", FrontendId.Yearly) |@|
        one[WeeklyDomestic[Month.type]](plans, "Weekly Domestic month", FrontendId.Monthly) |@|
        one[WeeklyDomestic[OneYear.type]](plans, "Weekly Domestic one year", FrontendId.OneYear) |@|
        one[WeeklyDomestic[ThreeMonths.type]](plans, "Weekly Domestic three months", FrontendId.ThreeMonths)
      ) (WeeklyDomesticPlans)
    weeklyRestOfWorld <- (
      one[WeeklyRestOfWorld[SixWeeks.type]](plans, "Weekly Rest of World Six weeks", FrontendId.SixWeeks) |@|
        one[WeeklyRestOfWorld[Quarter.type]](plans, "Weekly Rest of World quarter", FrontendId.Quarterly) |@|
        one[WeeklyRestOfWorld[Year.type]](plans, "Weekly Rest of World year", FrontendId.Yearly) |@|
        one[WeeklyRestOfWorld[Month.type]](plans, "Weekly Rest of World month", FrontendId.Monthly) |@|
        one[WeeklyRestOfWorld[OneYear.type]](plans, "Weekly Rest of World one year", FrontendId.OneYear) |@|
        one[WeeklyRestOfWorld[ThreeMonths.type]](plans, "Weekly Rest of World three months", FrontendId.ThreeMonths)
      ) (WeeklyRestOfWorldPlans)
    weekly = WeeklyPlans(weeklyZoneA, weeklyZoneB, weeklyZoneC, weeklyDomestic, weeklyRestOfWorld)

    map <- Validation.s[NonEmptyList[String]](plans.map(p => p.id -> p).toMap)
  } yield Catalog(digipack, supporterPlus, tierThree, contributor, voucher, digitalVoucher, delivery, nationalDelivery, weekly, map)


  lazy val catalog: M[NonEmptyList[String] \/ Catalog] = (for {
    plans <- EitherT[String, M, List[CatalogZuoraPlan]](joinUp).leftMap(_.wrapNel)
    catalog <- EitherT(validatePlans(plans).toDisjunction.point[M])
  } yield catalog).run

  lazy val unsafeCatalog: Catalog = unsafeGet(catalog.map(
    _.valueOr(e => throw new IllegalStateException(s"$e while getting catalog"))
  ))
}
