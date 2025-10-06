package com.github.oogasawa.utility.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.oogasawa.utility.cli.UtilityCliHelpFormatter.Section;

/**
 * Builder capturing layout configuration for {@link UtilityCliHelpFormatter}. The builder stores an
 * ordered list of sections and formatting parameters (width, indentation) that are materialised into
 * a formatter instance just before rendering.
 */
public class UtilityCliHelpFormatterBuilder {

    private final List<Section> sections = new ArrayList<>();
    private Integer width;
    private Integer leftPadding;
    private Integer descPadding;

    public UtilityCliHelpFormatterBuilder width(int value) {
        this.width = value;
        return this;
    }

    public UtilityCliHelpFormatterBuilder leftPadding(int value) {
        this.leftPadding = value;
        return this;
    }

    public UtilityCliHelpFormatterBuilder descPadding(int value) {
        this.descPadding = value;
        return this;
    }

    /**
     * Adds a usage section that will render the Commons CLI usage string.
     */
    public UtilityCliHelpFormatterBuilder addUsageSection(String heading) {
        this.sections.add(Section.usage(heading));
        return this;
    }

    /**
     * Adds an options section that renders all options registered for the command.
     */
    public UtilityCliHelpFormatterBuilder addOptionsSection(String heading) {
        this.sections.add(Section.options(heading));
        return this;
    }

    /**
     * Adds a section that prints the command description stored in the repository.
     */
    public UtilityCliHelpFormatterBuilder addCommandDescriptionSection(String heading) {
        this.sections.add(Section.commandDescription(heading));
        return this;
    }

    /**
     * Adds a custom section with arbitrary content. Each {@code line} may contain embedded
     * newlines for multi-line paragraphs.
     */
    public UtilityCliHelpFormatterBuilder addCustomSection(String heading,
            Collection<String> lines) {
        this.sections.add(Section.custom(heading, lines));
        return this;
    }

    /**
     * Removes any previously configured sections.
     */
    public UtilityCliHelpFormatterBuilder clearSections() {
        this.sections.clear();
        return this;
    }

    /**
     * @return {@code true} when at least one section has been configured
     */
    public boolean hasSections() {
        return !this.sections.isEmpty();
    }

    List<Section> getSections() {
        return List.copyOf(this.sections);
    }

    UtilityCliHelpFormatterBuilder replaceSections(List<Section> newSections) {
        this.sections.clear();
        if (newSections != null) {
            this.sections.addAll(newSections);
        }
        return this;
    }

    public UtilityCliHelpFormatterBuilder mergeFrom(UtilityCliHelpFormatterBuilder other) {
        if (other == null) {
            return this;
        }
        if (other.width != null) {
            this.width = other.width;
        }
        if (other.leftPadding != null) {
            this.leftPadding = other.leftPadding;
        }
        if (other.descPadding != null) {
            this.descPadding = other.descPadding;
        }
        if (!other.sections.isEmpty()) {
            this.sections.clear();
            this.sections.addAll(other.sections);
        }
        return this;
    }

    public UtilityCliHelpFormatterBuilder copy() {
        UtilityCliHelpFormatterBuilder copy = new UtilityCliHelpFormatterBuilder();
        copy.width = this.width;
        copy.leftPadding = this.leftPadding;
        copy.descPadding = this.descPadding;
        copy.sections.addAll(this.sections);
        return copy;
    }

    public UtilityCliHelpFormatter build() {
        UtilityCliHelpFormatter formatter = new UtilityCliHelpFormatter();
        if (this.width != null) {
            formatter.setWidth(this.width);
        }
        if (this.leftPadding != null) {
            formatter.setLeftPadding(this.leftPadding);
        }
        if (this.descPadding != null) {
            formatter.setDescPadding(this.descPadding);
        }
        formatter.sections(this.sections);
        return formatter;
    }
}
