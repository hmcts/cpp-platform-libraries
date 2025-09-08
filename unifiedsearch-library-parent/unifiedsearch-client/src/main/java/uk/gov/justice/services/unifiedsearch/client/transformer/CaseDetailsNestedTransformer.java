package uk.gov.justice.services.unifiedsearch.client.transformer;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

import uk.gov.justice.services.unifiedsearch.client.domain.Address;
import uk.gov.justice.services.unifiedsearch.client.domain.Application;
import uk.gov.justice.services.unifiedsearch.client.domain.CaseDetails;
import uk.gov.justice.services.unifiedsearch.client.domain.CourtOrder;
import uk.gov.justice.services.unifiedsearch.client.domain.Hearing;
import uk.gov.justice.services.unifiedsearch.client.domain.Offence;
import uk.gov.justice.services.unifiedsearch.client.domain.Party;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.commons.collections.CollectionUtils;

@ApplicationScoped
public class CaseDetailsNestedTransformer {

    @Inject
    private Logger logger;

    public void mergeParties(final CaseDetails incomingIndexData, final CaseDetails existingIndexData) {

        final List<Party> incomingIndexDataParties = incomingIndexData.getParties();
        if (incomingIndexDataParties != null) {

            final List<Party> existingIndexDataParties = existingIndexData.getParties();

            final Set<String> partyIds = incomingIndexDataParties.stream().map(Party::getPartyId).collect(toSet());

            if (null != existingIndexDataParties) {
                final List<Party> existingIndexPartiesToMerge = existingIndexDataParties
                        .stream()
                        .filter(party -> partyIds.contains(party.getPartyId()))
                        .collect(toList());

                mergeMissingIncomingIndexDataPartyDefendantAttributes(existingIndexPartiesToMerge, incomingIndexDataParties);
                existingIndexDataParties.removeIf(party -> partyIds.contains(party.getPartyId()));

                mergeAliases(existingIndexPartiesToMerge, incomingIndexDataParties);
                mergeOffences(existingIndexPartiesToMerge, incomingIndexDataParties);

                existingIndexDataParties.addAll(incomingIndexDataParties);
            } else {
                existingIndexData.setParties(incomingIndexDataParties);
            }
        }
    }

    private void mergeMissingIncomingIndexDataPartyDefendantAttributes(final List<Party> existingIndexPartiesToMerge, final List<Party> incomingIndexDataParties) {

        existingIndexPartiesToMerge.forEach(party -> {
            fromPartyList(party.getPartyId(), incomingIndexDataParties)
                    .ifPresent(tp -> mergeMissingPartyAttributes.apply(party, tp));
        });
    }


    private void mergeAliases(final List<Party> existingIndexParties, final List<Party> incomingIndexDataParties) {
        existingIndexParties.forEach(party -> {
            fromPartyList(party.getPartyId(), incomingIndexDataParties)
                    .ifPresent(tp -> mergePartyAliases.apply(party, tp));
        });
    }

    private void mergeOffences(final List<Party> existingIndexParties, final List<Party> incomingIndexDataParties) {
        existingIndexParties.forEach(existingParty -> {
            fromPartyList(existingParty.getPartyId(), incomingIndexDataParties)
                    .ifPresent(tp -> mergePartyOffences.apply(existingParty, tp));
        });
    }

    private Optional<Party> fromPartyList(final String partyId, final List<Party> partyList) {
        return partyList.stream().filter(party -> party.getPartyId().equals(partyId)).findFirst();
    }

    private Optional<Offence> fromOffenceList(final String offenceId, final List<Offence> offenceList) {
        return offenceList.stream().filter(offence -> offence.getOffenceId().equals(offenceId)).findFirst();
    }

    private BiFunction<Party, Party, Party> mergeMissingPartyAttributes = (existingParty, targetParty) -> {

        if (isBlank(targetParty.getFirstName())) {
            targetParty.setFirstName(existingParty.getFirstName());
        }

        if (isBlank(targetParty.getMiddleName())) {
            targetParty.setMiddleName(existingParty.getMiddleName());
        }

        if (isBlank(targetParty.getLastName())) {
            targetParty.setLastName(existingParty.getLastName());
        }

        if (isBlank(targetParty.getTitle())) {
            targetParty.setTitle(existingParty.getTitle());
        }

        if (isBlank(targetParty.getDateOfBirth())) {
            targetParty.setDateOfBirth(existingParty.getDateOfBirth());
        }

        if (isBlank(targetParty.getGender())) {
            targetParty.setGender(existingParty.getGender());
        }

        if (isBlank(targetParty.getAddressLines())) {
            targetParty.setAddressLines(existingParty.getAddressLines());
        }

        if (isBlank(targetParty.getMasterPartyId())) {
            targetParty.setMasterPartyId(existingParty.getMasterPartyId());
        }
        final Address existingDefendantAddress = existingParty.getDefendantAddress();

        if (null == targetParty.getDefendantAddress() && null != existingDefendantAddress) {
            Address defendantAddress = new Address();
            defendantAddress.setAddress1(existingDefendantAddress.getAddress1());
            defendantAddress.setAddress2(existingDefendantAddress.getAddress2());
            defendantAddress.setAddress3(existingDefendantAddress.getAddress3());
            defendantAddress.setAddress4(existingDefendantAddress.getAddress4());
            defendantAddress.setAddress5(existingDefendantAddress.getAddress5());
            defendantAddress.setPostCode(existingDefendantAddress.getPostCode());
            targetParty.setDefendantAddress(defendantAddress);
        }

        if (isBlank(targetParty.getCroNumber())) {
            targetParty.setCroNumber(existingParty.getCroNumber());
        }

        if (isBlank(targetParty.getCourtProceedingsInitiated())) {
            targetParty.setCourtProceedingsInitiated(existingParty.getCourtProceedingsInitiated());
        }

        if (isBlank(targetParty.getPncId())) {
            targetParty.setPncId(existingParty.getPncId());
        }

        if (isBlank(targetParty.getPostCode())) {
            targetParty.setPostCode(existingParty.getPostCode());
        }

        if (isBlank(targetParty.getPncId())) {
            targetParty.setPncId(existingParty.getPncId());
        }

        if (isBlank(targetParty.getArrestSummonsNumber())) {
            targetParty.setArrestSummonsNumber(existingParty.getArrestSummonsNumber());
        }

        if (isBlank(targetParty.getOrganisationName())) {
            targetParty.setOrganisationName(existingParty.getOrganisationName());
        }

        if (isBlank(targetParty.getNationalInsuranceNumber())) {
            targetParty.setNationalInsuranceNumber(existingParty.getNationalInsuranceNumber());
        }

        if (targetParty.getProceedingsConcluded()  == null) {
            targetParty.setProceedingsConcluded(existingParty.getProceedingsConcluded());
        }

        if (targetParty.getRepresentationOrder() == null) {
            targetParty.setRepresentationOrder(existingParty.getRepresentationOrder());
        }

        return targetParty;
    };

    private BiFunction<Party, Party, Party> mergePartyAliases = (party, targetParty) -> {
        if (party.getAliases() != null) {
            if (targetParty.getAliases() != null) {
                targetParty.getAliases().addAll(party.getAliases());
            }
            if (targetParty.getAliases() == null) {
                targetParty.setAliases(party.getAliases());
            }
        }
        return targetParty;
    };

    private BiFunction<Offence, Offence, Offence> mergeOffenceCourtOrders = (offence, targetOffence) -> {
        final List<CourtOrder> courtOrders = offence.getCourtOrders();

        if (CollectionUtils.isNotEmpty(courtOrders)) {
            final List<CourtOrder> targetCourtOrders = targetOffence.getCourtOrders();

            if (CollectionUtils.isNotEmpty(targetCourtOrders)) {
                final Set<String> targetCourtOrderIds = targetCourtOrders.stream()
                        .map(CourtOrder::getId)
                        .collect(toSet());

                courtOrders.stream()
                        .filter(o -> !targetCourtOrderIds.contains(o.getId()))
                        .forEach(targetCourtOrders::add);
            } else {
                targetOffence.setCourtOrders(courtOrders);
            }
        }
        return targetOffence;
    };

    private BiFunction<Party, Party, Party> mergePartyOffences = (party,    targetParty) -> {
        final List<Offence> partyOffences = party.getOffences();
        if (partyOffences != null) {

            final List<Offence> targetPartyOffences = targetParty.getOffences();
            if (targetPartyOffences != null) {

                final Set<String> targetOffenceIds = targetPartyOffences.stream()
                        .map(Offence::getOffenceId)
                        .collect(toSet());

                partyOffences.stream()
                        .filter(o -> !targetOffenceIds.contains(o.getOffenceId()))
                        .forEach(targetPartyOffences::add);

                partyOffences.stream()
                        .forEach(partyOffence -> {
                            fromOffenceList(partyOffence.getOffenceId(), targetPartyOffences)
                                    .ifPresent(targetOffence -> mergeOffenceCourtOrders.apply(partyOffence, targetOffence));
                        });
            }
            else {
                targetParty.setOffences(partyOffences);
            }
        }
        return targetParty;
    };



    public void mergeHearings(final CaseDetails incomingIndexData, final CaseDetails existingIndexData) {
        if (incomingIndexData.getHearings() != null) {

            final List<Hearing> incomingIndexDataHearings = incomingIndexData.getHearings();
            final List<Hearing> existingSearchIndexDataHearings = existingIndexData.getHearings();

            if (null != existingSearchIndexDataHearings) {
                if(logger.isInfoEnabled()) {
                    incomingIndexDataHearings.stream().forEach(hearing -> logIncomingHearingDetails(hearing));
                }
                final Set<String> hearingIds = incomingIndexDataHearings.stream().map(Hearing::getHearingId).collect(toSet());
                existingSearchIndexDataHearings.removeIf(hearing -> hearingIds.contains(hearing.getHearingId()));
                existingSearchIndexDataHearings.addAll(incomingIndexDataHearings);
            } else {
                existingIndexData.setHearings(incomingIndexDataHearings);
            }
        }
    }

    public void mergeApplications(final CaseDetails incomingIndexData, final CaseDetails existingIndexData) {
        if (incomingIndexData.getApplications() != null) {
            final List<Application> incomingIndexDataApplications = incomingIndexData.getApplications();
            final List<Application> existingIndexDataApplications = existingIndexData.getApplications();

            if (null != existingIndexDataApplications) {
                final Set<String> applicationIds = incomingIndexDataApplications.stream().map(Application::getApplicationId).collect(toSet());

                existingIndexDataApplications.removeIf(application -> applicationIds.contains(application.getApplicationId()));
                existingIndexDataApplications.addAll(incomingIndexDataApplications);
            } else {
                existingIndexData.setApplications(incomingIndexDataApplications);
            }
        }
    }


    private void logIncomingHearingDetails(final Hearing hearing ) {
        if(null != hearing) {
            this.logger.info("New hearing data with  timeNow: {}, hearingId: {},  estimatedDuration: {}, defenceCounsels: {}" , LocalDate.now(), hearing.getHearingId(), hearing.getEstimatedDuration(), hearing.getDefenceCounsels());
        }

    }


}
