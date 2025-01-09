package org.seng302.repositories;

import org.seng302.models.Card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.transaction.annotation.Transactional;
import org.seng302.models.*;

import java.util.List;

/**
 * JPA Repository for the Card entity/table.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Get Card by it's ID
     *
     * @param id
     * @return Card
     */
    Card findCardById(long id);

    /**
     * Gets cards with a matching keywords string
     * todo: get matches for single keywords instead of exact matches
     *
     * @param keywords
     * @return List<Card> a list of cards matching keywords
     */
    List<Card> findCardsByKeywords(String keywords);

    /**
     * Search for cards with marketplace section type:
     * FORSALE, WANTED, EXCHANGE
     *
     * @param cardType enum of MarketplaceSection
     * @return List<Card> a list of cards matching cardType
     */
    List<Card> findInventoryByCardType(MarketplaceSection cardType);
}
