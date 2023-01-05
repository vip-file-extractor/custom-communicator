package com.evam.marketing.communication.repository.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Resource template
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Entity
@Table(
        name = "int_resource_template",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"code", "scenario_name", "scenario_version"}
        )
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceTemplate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "int_resource_template_seq")
    private Long id;
    @Column(name = "code")
    private String communicationCode;
    @Column(name = "scenario_name")
    private String scenarioName;
    @Column(name = "scenario_version")
    private int scenarioVersion;
    @Lob
    @Column(name = "content")
    private String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceTemplate that = (ResourceTemplate) o;
        return scenarioVersion == that.scenarioVersion
            && Objects.equals(communicationCode, that.communicationCode)
            && Objects.equals(scenarioName, that.scenarioName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(communicationCode, scenarioName, scenarioVersion);
    }
}
