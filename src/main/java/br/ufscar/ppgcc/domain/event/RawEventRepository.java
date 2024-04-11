package br.ufscar.ppgcc.domain.event;

import br.ufscar.ppgcc.data.RawEvent;
import org.springframework.data.repository.Repository;

import java.util.UUID;

interface RawEventRepository extends Repository<RawEvent, UUID> {

    void save(RawEvent rawEvent);

}
