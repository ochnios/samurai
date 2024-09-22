package pl.ochnios.ninjabe.model.entities;

import pl.ochnios.ninjabe.model.dtos.PatchDto;

public interface PatchableEntity extends AppEntity {

    PatchDto getPatchDto();

    void apply(PatchDto patchDto);
}
