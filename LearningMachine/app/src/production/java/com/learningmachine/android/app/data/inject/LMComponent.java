package com.hyland.android.app.data.inject;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {LMModule.class, ProductionDataModule.class, ApiModule.class})
public interface LMComponent extends LMGraph {

    final class Initializer {
        public static LMGraph init(Application application) {
            return DaggerLMComponent.builder()
                    .lMModule(new LMModule(application))
                    .build();
        }
    }
}
