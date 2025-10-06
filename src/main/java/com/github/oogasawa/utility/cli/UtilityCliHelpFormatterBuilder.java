package com.github.oogasawa.utility.cli;

import java.util.Collection;
import java.util.List;

/**
 * Builder that captures configuration for {@link UtilityCliHelpFormatter} instances.
 * Storing the builder alongside each command allows rendering a fresh formatter per call
 * while keeping the configuration immutable and easily testable.
 */
public class UtilityCliHelpFormatterBuilder {

    private String usageHeading;
    private String descriptionHeading;
    private String optionsHeading;
    private String examplesHeading;
    private String description;
    private List<String> examples;
    private Integer width;
    private Integer leftPadding;
    private Integer descPadding;

    public UtilityCliHelpFormatterBuilder usageHeading(String heading) {
        this.usageHeading = heading;
        return this;
    }

    public UtilityCliHelpFormatterBuilder descriptionHeading(String heading) {
        this.descriptionHeading = heading;
        return this;
    }

    public UtilityCliHelpFormatterBuilder optionsHeading(String heading) {
        this.optionsHeading = heading;
        return this;
    }

    public UtilityCliHelpFormatterBuilder examplesHeading(String heading) {
        this.examplesHeading = heading;
        return this;
    }

    public UtilityCliHelpFormatterBuilder description(String value) {
        this.description = value;
        return this;
    }

    public UtilityCliHelpFormatterBuilder examples(Collection<String> values) {
        if (values == null) {
            this.examples = null;
        } else {
            this.examples = List.copyOf(values);
        }
        return this;
    }

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

    public UtilityCliHelpFormatterBuilder mergeFrom(UtilityCliHelpFormatterBuilder other) {
        if (other == null) {
            return this;
        }
        if (other.usageHeading != null) {
            this.usageHeading = other.usageHeading;
        }
        if (other.descriptionHeading != null) {
            this.descriptionHeading = other.descriptionHeading;
        }
        if (other.optionsHeading != null) {
            this.optionsHeading = other.optionsHeading;
        }
        if (other.examplesHeading != null) {
            this.examplesHeading = other.examplesHeading;
        }
        if (other.description != null) {
            this.description = other.description;
        }
        if (other.examples != null) {
            this.examples = List.copyOf(other.examples);
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
        return this;
    }

    public UtilityCliHelpFormatterBuilder copy() {
        UtilityCliHelpFormatterBuilder builder = new UtilityCliHelpFormatterBuilder();
        builder.usageHeading = this.usageHeading;
        builder.descriptionHeading = this.descriptionHeading;
        builder.optionsHeading = this.optionsHeading;
        builder.examplesHeading = this.examplesHeading;
        builder.description = this.description;
        if (this.examples != null) {
            builder.examples = List.copyOf(this.examples);
        }
        builder.width = this.width;
        builder.leftPadding = this.leftPadding;
        builder.descPadding = this.descPadding;
        return builder;
    }

    public UtilityCliHelpFormatter build() {
        UtilityCliHelpFormatter formatter = new UtilityCliHelpFormatter();
        if (this.width != null) {
            formatter.setWidth(this.width.intValue());
        }
        if (this.leftPadding != null) {
            formatter.setLeftPadding(this.leftPadding.intValue());
        }
        if (this.descPadding != null) {
            formatter.setDescPadding(this.descPadding.intValue());
        }
        if (this.usageHeading != null) {
            formatter.usageHeading(this.usageHeading);
        }
        if (this.descriptionHeading != null) {
            formatter.descriptionHeading(this.descriptionHeading);
        }
        if (this.optionsHeading != null) {
            formatter.optionsHeading(this.optionsHeading);
        }
        if (this.examplesHeading != null) {
            formatter.examplesHeading(this.examplesHeading);
        }
        if (this.description != null) {
            formatter.description(this.description);
        }
        if (this.examples != null) {
            formatter.examples(this.examples);
        }
        return formatter;
    }

    boolean hasDescription() {
        return this.description != null;
    }

    boolean hasOptionsHeading() {
        return this.optionsHeading != null;
    }
}
