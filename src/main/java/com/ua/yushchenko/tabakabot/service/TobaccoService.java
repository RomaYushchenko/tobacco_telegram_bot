package com.ua.yushchenko.tabakabot.service;

import java.util.ArrayList;
import java.util.List;

import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.domain.Tobacco;
import com.ua.yushchenko.tabakabot.model.mapper.TobaccoMapper;
import com.ua.yushchenko.tabakabot.model.persistence.TobaccoDb;
import com.ua.yushchenko.tabakabot.repository.TobaccoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service that exposes the base functionality for interacting with {@link Tobacco} data
 *
 * @author romanyushchenko
 * @version v.0.1
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TobaccoService {

    @NonNull
    private final TobaccoRepository tobaccoRepository;
    @NonNull
    private final TobaccoMapper tobaccoMapper;

    public void saveTobacco(final Tobacco tobacco){
        final TobaccoDb tobaccoDb = tobaccoMapper.domainToDb(tobacco);
        tobaccoRepository.save(tobaccoDb);
    }

    public List<Tobacco> getAllTobacco() {
        final List<Tobacco> tobaccos = new ArrayList<>();
        tobaccoRepository.findAll().forEach(tobaccoDb -> tobaccos.add(tobaccoMapper.dbToDomain(tobaccoDb)));
        return tobaccos;
    }
}
