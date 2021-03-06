package no.difi.vefa.validator.plugin;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Renderer;
import no.difi.vefa.validator.api.ValidatorPlugin;
import no.difi.vefa.validator.checker.SvrlXsltChecker;
import no.difi.vefa.validator.checker.XsdChecker;
import no.difi.vefa.validator.declaration.SbdhDeclaration;

import java.util.ArrayList;
import java.util.List;

public class SbdhPlugin implements ValidatorPlugin {

    @Override
    public List<String> capabilities() {
        return new ArrayList<String>() {{
            add("sbdh");
        }};
    }

    @Override
    public List<Class<? extends Checker>> checkers() {
        return new ArrayList<Class<? extends Checker>>() {{
            add(XsdChecker.class);
            add(SvrlXsltChecker.class);
        }};
    }

    @Override
    public List<Declaration> declarations() {
        return new ArrayList<Declaration>() {{
            add(new SbdhDeclaration());
        }};
    }

    @Override
    public List<Class<? extends Renderer>> renderers() {
        return new ArrayList<>();
    }
}
