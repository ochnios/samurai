package pl.ochnios.samurai.model.entities;

import pl.ochnios.samurai.model.dtos.PatchDto;

public interface PatchableEntity extends AppEntity {

    PatchDto getPatchDto();

    void apply(PatchDto patchDto);
}
