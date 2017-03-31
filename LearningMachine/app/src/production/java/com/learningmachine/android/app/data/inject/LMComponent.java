package com.learningmachine.android.app.data.inject;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {LMModule.class, ProductionDataModule.class})
public interface LMComponent extends LMGraph {

    final class Initializer {
        public static LMGraph init() {
            return DaggerLMComponent.builder()
                    .build();
        }
    }
}
