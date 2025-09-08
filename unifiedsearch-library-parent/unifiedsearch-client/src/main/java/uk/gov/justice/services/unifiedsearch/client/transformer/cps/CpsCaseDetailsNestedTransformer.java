package uk.gov.justice.services.unifiedsearch.client.transformer.cps;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import uk.gov.justice.services.unifiedsearch.client.domain.cps.CaseDetails;
import uk.gov.justice.services.unifiedsearch.client.domain.cps.Hearing;
import uk.gov.justice.services.unifiedsearch.client.domain.cps.LinkedCase;
import uk.gov.justice.services.unifiedsearch.client.domain.cps.Offence;
import uk.gov.justice.services.unifiedsearch.client.domain.cps.Party;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CpsCaseDetailsNestedTransformer {

    private BiFunction<Party, Party, Party> mergeMissingPartyAttributes = (existingParty, targetParty) -> {

        if (isBlank(targetParty.getFirstName())) {
            targetParty.setFirstName(existingParty.getFirstName());
        }

        if (isBlank(targetParty.getLastName())) {
            targetParty.setLastName(existingParty.getLastName());
        }

        if (isBlank(targetParty.getDateOfBirth())) {
            targetParty.setDateOfBirth(existingParty.getDateOfBirth());
        }

        if (isBlank(targetParty.getAsn())) {
            targetParty.setAsn(existingParty.getAsn());
        }

        if (isBlank(targetParty.getOicShoulderNumber())) {
            targetParty.setOicShoulderNumber(existingParty.getOicShoulderNumber());
        }

        if (isBlank(targetParty.getPartyId())) {
            targetParty.setPartyId(existingParty.getPartyId());
        }

        if (isBlank(targetParty.getPncId())) {
            targetParty.setPncId(existingParty.getPncId());
        }

        if (isNotEmpty(targetParty.get_party_type())) {
            targetParty.set_party_type(existingParty.get_party_type());
        }

        if (isBlank(targetParty.getPncId())) {
            targetParty.setPncId(existingParty.getPncId());
        }

        if (isBlank(targetParty.getOrganisationName())) {
            targetParty.setOrganisationName(existingParty.getOrganisationName());
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
    private BiFunction<Party, Party, Party> mergePartyOffences = (party, targetParty) -> {
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

            } else {
                targetParty.setOffences(partyOffences);
            }
        }
        return targetParty;
    };

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

    public void mergeHearings(final CaseDetails incomingIndexData, final CaseDetails existingIndexData) {
        if (incomingIndexData.getHearings() != null) {

            final List<Hearing> incomingIndexDataHearings = incomingIndexData.getHearings();
            final List<Hearing> existingSearchIndexDataHearings = existingIndexData.getHearings();

            if (null != existingSearchIndexDataHearings) {
                final Set<String> hearingIds = incomingIndexDataHearings.stream().map(Hearing::getHearingId).collect(toSet());

                existingSearchIndexDataHearings.removeIf(hearing -> hearingIds.contains(hearing.getHearingId()));
                existingSearchIndexDataHearings.addAll(incomingIndexDataHearings);
            } else {
                existingIndexData.setHearings(incomingIndexDataHearings);
            }
        }
    }

    public void mergeLinkedCases(final CaseDetails incomingIndexData, final CaseDetails existingIndexData) {
        if (incomingIndexData.getLinkedCases() != null) {
            final List<LinkedCase> incomingIndexDataLinkedCases = incomingIndexData.getLinkedCases();
            final List<LinkedCase> existingSearchIndexDataLinkedCases = existingIndexData.getLinkedCases();

            if (Boolean.FALSE.equals(incomingIndexData.getLinked())) {
                if(null != existingSearchIndexDataLinkedCases) {
                    final Set<String> LinkedCaseIds = incomingIndexDataLinkedCases.stream().map(LinkedCase::getLinkedCaseId).collect(toSet());
                    existingSearchIndexDataLinkedCases.removeIf(linkedCase -> LinkedCaseIds.contains(linkedCase.getLinkedCaseId()));
                }
            } else if (null != existingSearchIndexDataLinkedCases) {
                final Set<String> LinkedCaseIds = incomingIndexDataLinkedCases.stream().map(LinkedCase::getLinkedCaseId).collect(toSet());
                existingSearchIndexDataLinkedCases.removeIf(linkedCase -> LinkedCaseIds.contains(linkedCase.getLinkedCaseId()));
                existingSearchIndexDataLinkedCases.addAll(incomingIndexDataLinkedCases);
            } else {
                existingIndexData.setLinkedCases(incomingIndexDataLinkedCases);
            }
        }
    }

}
