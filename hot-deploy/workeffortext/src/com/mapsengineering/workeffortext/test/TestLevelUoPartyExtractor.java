package com.mapsengineering.workeffortext.test;

import java.util.List;

import org.ofbiz.base.util.GeneralException;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.util.LevelUoPartyExtractor;

public class TestLevelUoPartyExtractor extends BaseTestCase {
	
	private final String Y = "Y";
	private final String N = "N";
	private final String DSPL01 = "DSPL01";
	private final String SPL = "SPL";
	private final String DSSL01 = "DSSL01";
	private final String SSL = "SSL";
	private final String Company = "Company";
	private final String AMM = "AMM";
	
	/**
	 * test
	 * @throws GeneralException
	 */
	public void testLevelUoPartyExtractor() throws GeneralException {
		makeTestSameUO();
		makeTestParentUO();
		makeTestChildUO();
		makeTestSisterUO();
	}
	
	/**
	 * test same uo
	 * @throws GeneralException
	 */
	private void makeTestSameUO() throws GeneralException {
		LevelUoPartyExtractor levelUoPartyExtractorSame = getLevelUoPartyExtractor(DSPL01, SPL, Y, N, N, N);
		levelUoPartyExtractorSame.run();
		assertNotNullAndContains(levelUoPartyExtractorSame.getOrgUnitIdList(), DSPL01);
		assertNotNullAndContains(levelUoPartyExtractorSame.getWepaPartyIdList(), DSPL01);
	}
	
	/**
	 * test parent uo
	 * @throws GeneralException
	 */
	private void makeTestParentUO() throws GeneralException {
		LevelUoPartyExtractor levelUoPartyExtractorParent = getLevelUoPartyExtractor(DSPL01, SPL, N, Y, N, N);
		levelUoPartyExtractorParent.run();
		assertNotNullAndContains(levelUoPartyExtractorParent.getOrgUnitIdList(), Company);
		assertNotNullAndContains(levelUoPartyExtractorParent.getWepaPartyIdList(), Company);
	}
	
	/**
	 * test child uo
	 * @throws GeneralException
	 */
	private void makeTestChildUO() throws GeneralException {
		LevelUoPartyExtractor levelUoPartyExtractorChild = getLevelUoPartyExtractor(Company, AMM, N, N, Y, N);
		levelUoPartyExtractorChild.run();
		assertNotNullAndContains(levelUoPartyExtractorChild.getOrgUnitIdList(), DSPL01);
		assertNotNullAndContains(levelUoPartyExtractorChild.getWepaPartyIdList(), DSPL01);
	}
	
	/**
	 * test sister uo
	 * @throws GeneralException
	 */
	private void makeTestSisterUO() throws GeneralException {
		LevelUoPartyExtractor levelUoPartyExtractorSister = getLevelUoPartyExtractor(DSPL01, SPL, N, N, N, Y);
		levelUoPartyExtractorSister.run();
		assertNotNullAndContains(levelUoPartyExtractorSister.getOrgUnitIdList(), DSSL01);
		assertNotNullAndContains(levelUoPartyExtractorSister.getWepaPartyIdList(), DSSL01);
	}
	
	/**
	 * istanza il LevelUoPartyExtractor
	 * @param orgUnitId
	 * @param orgUnitRoleTypeId
	 * @param levelSame
	 * @param levelParent
	 * @param levelChild
	 * @param levelSister
	 * @return
	 */
	private LevelUoPartyExtractor getLevelUoPartyExtractor(String orgUnitId, String orgUnitRoleTypeId, String levelSame, String levelParent, String levelChild, String levelSister) {
		LevelUoPartyExtractor levelUoPartyExtractor = new LevelUoPartyExtractor(delegator, orgUnitId, orgUnitRoleTypeId);
		levelUoPartyExtractor.initLevelSameUO(levelSame, levelSame);
		levelUoPartyExtractor.initLevelParentUO(levelParent, levelParent);
		levelUoPartyExtractor.initLevelChildUO(levelChild, levelChild);
		levelUoPartyExtractor.initLevelSisterUO(levelSister, levelSister);
		levelUoPartyExtractor.initLevelTopUO(N, N);
		return levelUoPartyExtractor;
	}
	
	/**
	 * verifica che la lista contenga l elemento
	 * @param partyIdList
	 * @param partyId
	 */
	private void assertNotNullAndContains(List<String> partyIdList, String partyId) {
		assertNotNull(partyIdList);
		boolean contains = false;
		for (String item : partyIdList) {
			if (partyId.equals(item)) {
				contains = true;
				break;
			}
		}
		assertTrue(contains);
	}

}
