package org.pattonvillecs.pattonvilleapp.di.scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Mitchell on 10/4/2017.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerChildFragment {
}
