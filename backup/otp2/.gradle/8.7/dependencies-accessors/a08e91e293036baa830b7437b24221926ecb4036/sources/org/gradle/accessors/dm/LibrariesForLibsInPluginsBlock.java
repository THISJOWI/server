package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibsInPluginsBlock extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final ComLibraryAccessors laccForComLibraryAccessors = new ComLibraryAccessors(owner);
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibsInPluginsBlock(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>com</b>
     *
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public ComLibraryAccessors getCom() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForComLibraryAccessors;
    }

    /**
     * Group of libraries at <b>org</b>
     *
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public OrgLibraryAccessors getOrg() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForOrgLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     *
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public BundleAccessors getBundles() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class ComLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlLibraryAccessors laccForComFasterxmlLibraryAccessors = new ComFasterxmlLibraryAccessors(owner);
        private final ComGoogleLibraryAccessors laccForComGoogleLibraryAccessors = new ComGoogleLibraryAccessors(owner);

        public ComLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public ComFasterxmlLibraryAccessors getFasterxml() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForComFasterxmlLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.google</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public ComGoogleLibraryAccessors getGoogle() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForComGoogleLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class ComFasterxmlLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonLibraryAccessors laccForComFasterxmlJacksonLibraryAccessors = new ComFasterxmlJacksonLibraryAccessors(owner);

        public ComFasterxmlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public ComFasterxmlJacksonLibraryAccessors getJackson() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForComFasterxmlJacksonLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class ComFasterxmlJacksonLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonCoreLibraryAccessors laccForComFasterxmlJacksonCoreLibraryAccessors = new ComFasterxmlJacksonCoreLibraryAccessors(owner);

        public ComFasterxmlJacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.core</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public ComFasterxmlJacksonCoreLibraryAccessors getCore() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForComFasterxmlJacksonCoreLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class ComFasterxmlJacksonCoreLibraryAccessors extends SubDependencyFactory {
        private final ComFasterxmlJacksonCoreJacksonLibraryAccessors laccForComFasterxmlJacksonCoreJacksonLibraryAccessors = new ComFasterxmlJacksonCoreJacksonLibraryAccessors(owner);

        public ComFasterxmlJacksonCoreLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.fasterxml.jackson.core.jackson</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public ComFasterxmlJacksonCoreJacksonLibraryAccessors getJackson() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForComFasterxmlJacksonCoreJacksonLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class ComFasterxmlJacksonCoreJacksonLibraryAccessors extends SubDependencyFactory {

        public ComFasterxmlJacksonCoreJacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>databind</b> with <b>com.fasterxml.jackson.core:jackson-databind</b> coordinates and
         * with version reference <b>com.fasterxml.jackson.core.jackson.databind</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getDatabind() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("com.fasterxml.jackson.core.jackson.databind");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class ComGoogleLibraryAccessors extends SubDependencyFactory {
        private final ComGoogleZxingLibraryAccessors laccForComGoogleZxingLibraryAccessors = new ComGoogleZxingLibraryAccessors(owner);

        public ComGoogleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.google.zxing</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public ComGoogleZxingLibraryAccessors getZxing() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForComGoogleZxingLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class ComGoogleZxingLibraryAccessors extends SubDependencyFactory {

        public ComGoogleZxingLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>com.google.zxing:core</b> coordinates and
         * with version reference <b>com.google.zxing.core</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getCore() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("com.google.zxing.core");
        }

        /**
         * Dependency provider for <b>javase</b> with <b>com.google.zxing:javase</b> coordinates and
         * with version reference <b>com.google.zxing.javase</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getJavase() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("com.google.zxing.javase");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgPostgresqlLibraryAccessors laccForOrgPostgresqlLibraryAccessors = new OrgPostgresqlLibraryAccessors(owner);
        private final OrgProjectlombokLibraryAccessors laccForOrgProjectlombokLibraryAccessors = new OrgProjectlombokLibraryAccessors(owner);
        private final OrgSpringdocLibraryAccessors laccForOrgSpringdocLibraryAccessors = new OrgSpringdocLibraryAccessors(owner);
        private final OrgSpringframeworkLibraryAccessors laccForOrgSpringframeworkLibraryAccessors = new OrgSpringframeworkLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.postgresql</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgPostgresqlLibraryAccessors getPostgresql() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgPostgresqlLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.projectlombok</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgProjectlombokLibraryAccessors getProjectlombok() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgProjectlombokLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springdoc</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringdocLibraryAccessors getSpringdoc() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringdocLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkLibraryAccessors getSpringframework() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgPostgresqlLibraryAccessors extends SubDependencyFactory {

        public OrgPostgresqlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>postgresql</b> with <b>org.postgresql:postgresql</b> coordinates and
         * with version reference <b>org.postgresql.postgresql</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getPostgresql() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.postgresql.postgresql");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgProjectlombokLibraryAccessors extends SubDependencyFactory {

        public OrgProjectlombokLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>lombok</b> with <b>org.projectlombok:lombok</b> coordinates and
         * with version reference <b>org.projectlombok.lombok</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getLombok() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.projectlombok.lombok");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringdocLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringdocSpringdocLibraryAccessors laccForOrgSpringdocSpringdocLibraryAccessors = new OrgSpringdocSpringdocLibraryAccessors(owner);

        public OrgSpringdocLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springdoc.springdoc</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringdocSpringdocLibraryAccessors getSpringdoc() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringdocSpringdocLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringdocSpringdocLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringdocSpringdocOpenapiLibraryAccessors laccForOrgSpringdocSpringdocOpenapiLibraryAccessors = new OrgSpringdocSpringdocOpenapiLibraryAccessors(owner);

        public OrgSpringdocSpringdocLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springdoc.springdoc.openapi</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringdocSpringdocOpenapiLibraryAccessors getOpenapi() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringdocSpringdocOpenapiLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringdocSpringdocOpenapiLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringdocSpringdocOpenapiStarterLibraryAccessors laccForOrgSpringdocSpringdocOpenapiStarterLibraryAccessors = new OrgSpringdocSpringdocOpenapiStarterLibraryAccessors(owner);

        public OrgSpringdocSpringdocOpenapiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springdoc.springdoc.openapi.starter</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringdocSpringdocOpenapiStarterLibraryAccessors getStarter() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringdocSpringdocOpenapiStarterLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringdocSpringdocOpenapiStarterLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringdocSpringdocOpenapiStarterWebmvcLibraryAccessors laccForOrgSpringdocSpringdocOpenapiStarterWebmvcLibraryAccessors = new OrgSpringdocSpringdocOpenapiStarterWebmvcLibraryAccessors(owner);

        public OrgSpringdocSpringdocOpenapiStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springdoc.springdoc.openapi.starter.webmvc</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringdocSpringdocOpenapiStarterWebmvcLibraryAccessors getWebmvc() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringdocSpringdocOpenapiStarterWebmvcLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringdocSpringdocOpenapiStarterWebmvcLibraryAccessors extends SubDependencyFactory {

        public OrgSpringdocSpringdocOpenapiStarterWebmvcLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>ui</b> with <b>org.springdoc:springdoc-openapi-starter-webmvc-ui</b> coordinates and
         * with version reference <b>org.springdoc.springdoc.openapi.starter.webmvc.ui</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getUi() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springdoc.springdoc.openapi.starter.webmvc.ui");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootLibraryAccessors laccForOrgSpringframeworkBootLibraryAccessors = new OrgSpringframeworkBootLibraryAccessors(owner);
        private final OrgSpringframeworkCloudLibraryAccessors laccForOrgSpringframeworkCloudLibraryAccessors = new OrgSpringframeworkCloudLibraryAccessors(owner);
        private final OrgSpringframeworkKafkaLibraryAccessors laccForOrgSpringframeworkKafkaLibraryAccessors = new OrgSpringframeworkKafkaLibraryAccessors(owner);
        private final OrgSpringframeworkSecurityLibraryAccessors laccForOrgSpringframeworkSecurityLibraryAccessors = new OrgSpringframeworkSecurityLibraryAccessors(owner);

        public OrgSpringframeworkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkBootLibraryAccessors getBoot() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkBootLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework.cloud</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkCloudLibraryAccessors getCloud() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkCloudLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework.kafka</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkKafkaLibraryAccessors getKafka() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkKafkaLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework.security</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkSecurityLibraryAccessors getSecurity() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkSecurityLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringLibraryAccessors laccForOrgSpringframeworkBootSpringLibraryAccessors = new OrgSpringframeworkBootSpringLibraryAccessors(owner);

        public OrgSpringframeworkBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkBootSpringLibraryAccessors getSpring() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkBootSpringLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkBootSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootLibraryAccessors laccForOrgSpringframeworkBootSpringBootLibraryAccessors = new OrgSpringframeworkBootSpringBootLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkBootSpringBootLibraryAccessors getBoot() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkBootSpringBootLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkBootSpringBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootDockerLibraryAccessors laccForOrgSpringframeworkBootSpringBootDockerLibraryAccessors = new OrgSpringframeworkBootSpringBootDockerLibraryAccessors(owner);
        private final OrgSpringframeworkBootSpringBootStarterLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.docker</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkBootSpringBootDockerLibraryAccessors getDocker() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkBootSpringBootDockerLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.starter</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors getStarter() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkBootSpringBootDockerLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkBootSpringBootDockerLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>compose</b> with <b>org.springframework.boot:spring-boot-docker-compose</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.docker.compose</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getCompose() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.boot.spring.boot.docker.compose");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkBootSpringBootStarterLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterDataLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>security</b> with <b>org.springframework.boot:spring-boot-starter-security</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.security</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getSecurity() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.boot.spring.boot.starter.security");
        }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.boot:spring-boot-starter-test</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.test</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getTest() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.boot.spring.boot.starter.test");
        }

        /**
         * Dependency provider for <b>web</b> with <b>org.springframework.boot:spring-boot-starter-web</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.web</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getWeb() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.boot.spring.boot.starter.web");
        }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.starter.data</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors getData() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkBootSpringBootStarterDataLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkBootSpringBootStarterDataLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jpa</b> with <b>org.springframework.boot:spring-boot-starter-data-jpa</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.data.jpa</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getJpa() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.boot.spring.boot.starter.data.jpa");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkCloudLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringLibraryAccessors laccForOrgSpringframeworkCloudSpringLibraryAccessors = new OrgSpringframeworkCloudSpringLibraryAccessors(owner);

        public OrgSpringframeworkCloudLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkCloudSpringLibraryAccessors getSpring() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkCloudSpringLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkCloudSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudLibraryAccessors = new OrgSpringframeworkCloudSpringCloudLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkCloudSpringCloudLibraryAccessors getCloud() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkCloudSpringCloudLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkCloudSpringCloudLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors getStarter() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkCloudSpringCloudStarterLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>bootstrap</b> with <b>org.springframework.cloud:spring-cloud-starter-bootstrap</b> coordinates and
         * with version reference <b>org.springframework.cloud.spring.cloud.starter.bootstrap</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getBootstrap() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.cloud.spring.cloud.starter.bootstrap");
        }

        /**
         * Dependency provider for <b>config</b> with <b>org.springframework.cloud:spring-cloud-starter-config</b> coordinates and
         * with version reference <b>org.springframework.cloud.spring.cloud.starter.config</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getConfig() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.cloud.spring.cloud.starter.config");
        }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter.netflix</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors getNetflix() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors getEureka() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>client</b> with <b>org.springframework.cloud:spring-cloud-starter-netflix-eureka-client</b> coordinates and
         * with version reference <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka.client</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getClient() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.cloud.spring.cloud.starter.netflix.eureka.client");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkKafkaLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkKafkaSpringLibraryAccessors laccForOrgSpringframeworkKafkaSpringLibraryAccessors = new OrgSpringframeworkKafkaSpringLibraryAccessors(owner);

        public OrgSpringframeworkKafkaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.kafka.spring</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkKafkaSpringLibraryAccessors getSpring() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkKafkaSpringLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkKafkaSpringLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkKafkaSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>kafka</b> with <b>org.springframework.kafka:spring-kafka</b> coordinates and
         * with version reference <b>org.springframework.kafka.spring.kafka</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getKafka() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.kafka.spring.kafka");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkSecurityLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkSecuritySpringLibraryAccessors laccForOrgSpringframeworkSecuritySpringLibraryAccessors = new OrgSpringframeworkSecuritySpringLibraryAccessors(owner);

        public OrgSpringframeworkSecurityLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.security.spring</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkSecuritySpringLibraryAccessors getSpring() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkSecuritySpringLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkSecuritySpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkSecuritySpringSecurityLibraryAccessors laccForOrgSpringframeworkSecuritySpringSecurityLibraryAccessors = new OrgSpringframeworkSecuritySpringSecurityLibraryAccessors(owner);

        public OrgSpringframeworkSecuritySpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.security.spring.security</b>
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public OrgSpringframeworkSecuritySpringSecurityLibraryAccessors getSecurity() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForOrgSpringframeworkSecuritySpringSecurityLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OrgSpringframeworkSecuritySpringSecurityLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkSecuritySpringSecurityLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.security:spring-security-test</b> coordinates and
         * with version reference <b>org.springframework.security.spring.security.test</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         *
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public Provider<MinimalExternalModuleDependency> getTest() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("org.springframework.security.spring.security.test");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final ComVersionAccessors vaccForComVersionAccessors = new ComVersionAccessors(providers, config);
        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com</b>
         */
        public ComVersionAccessors getCom() {
            return vaccForComVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org</b>
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class ComVersionAccessors extends VersionFactory  {

        private final ComFasterxmlVersionAccessors vaccForComFasterxmlVersionAccessors = new ComFasterxmlVersionAccessors(providers, config);
        private final ComGoogleVersionAccessors vaccForComGoogleVersionAccessors = new ComGoogleVersionAccessors(providers, config);
        public ComVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml</b>
         */
        public ComFasterxmlVersionAccessors getFasterxml() {
            return vaccForComFasterxmlVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.google</b>
         */
        public ComGoogleVersionAccessors getGoogle() {
            return vaccForComGoogleVersionAccessors;
        }

    }

    public static class ComFasterxmlVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonVersionAccessors vaccForComFasterxmlJacksonVersionAccessors = new ComFasterxmlJacksonVersionAccessors(providers, config);
        public ComFasterxmlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson</b>
         */
        public ComFasterxmlJacksonVersionAccessors getJackson() {
            return vaccForComFasterxmlJacksonVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonCoreVersionAccessors vaccForComFasterxmlJacksonCoreVersionAccessors = new ComFasterxmlJacksonCoreVersionAccessors(providers, config);
        public ComFasterxmlJacksonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.core</b>
         */
        public ComFasterxmlJacksonCoreVersionAccessors getCore() {
            return vaccForComFasterxmlJacksonCoreVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreVersionAccessors extends VersionFactory  {

        private final ComFasterxmlJacksonCoreJacksonVersionAccessors vaccForComFasterxmlJacksonCoreJacksonVersionAccessors = new ComFasterxmlJacksonCoreJacksonVersionAccessors(providers, config);
        public ComFasterxmlJacksonCoreVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.fasterxml.jackson.core.jackson</b>
         */
        public ComFasterxmlJacksonCoreJacksonVersionAccessors getJackson() {
            return vaccForComFasterxmlJacksonCoreJacksonVersionAccessors;
        }

    }

    public static class ComFasterxmlJacksonCoreJacksonVersionAccessors extends VersionFactory  {

        public ComFasterxmlJacksonCoreJacksonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.fasterxml.jackson.core.jackson.databind</b> with value <b>2.19.4</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getDatabind() { return getVersion("com.fasterxml.jackson.core.jackson.databind"); }

    }

    public static class ComGoogleVersionAccessors extends VersionFactory  {

        private final ComGoogleZxingVersionAccessors vaccForComGoogleZxingVersionAccessors = new ComGoogleZxingVersionAccessors(providers, config);
        public ComGoogleVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.google.zxing</b>
         */
        public ComGoogleZxingVersionAccessors getZxing() {
            return vaccForComGoogleZxingVersionAccessors;
        }

    }

    public static class ComGoogleZxingVersionAccessors extends VersionFactory  {

        public ComGoogleZxingVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.google.zxing.core</b> with value <b>3.5.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCore() { return getVersion("com.google.zxing.core"); }

        /**
         * Version alias <b>com.google.zxing.javase</b> with value <b>3.5.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJavase() { return getVersion("com.google.zxing.javase"); }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgPostgresqlVersionAccessors vaccForOrgPostgresqlVersionAccessors = new OrgPostgresqlVersionAccessors(providers, config);
        private final OrgProjectlombokVersionAccessors vaccForOrgProjectlombokVersionAccessors = new OrgProjectlombokVersionAccessors(providers, config);
        private final OrgSpringdocVersionAccessors vaccForOrgSpringdocVersionAccessors = new OrgSpringdocVersionAccessors(providers, config);
        private final OrgSpringframeworkVersionAccessors vaccForOrgSpringframeworkVersionAccessors = new OrgSpringframeworkVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.postgresql</b>
         */
        public OrgPostgresqlVersionAccessors getPostgresql() {
            return vaccForOrgPostgresqlVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.projectlombok</b>
         */
        public OrgProjectlombokVersionAccessors getProjectlombok() {
            return vaccForOrgProjectlombokVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springdoc</b>
         */
        public OrgSpringdocVersionAccessors getSpringdoc() {
            return vaccForOrgSpringdocVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework</b>
         */
        public OrgSpringframeworkVersionAccessors getSpringframework() {
            return vaccForOrgSpringframeworkVersionAccessors;
        }

    }

    public static class OrgPostgresqlVersionAccessors extends VersionFactory  {

        public OrgPostgresqlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.postgresql.postgresql</b> with value <b>42.6.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPostgresql() { return getVersion("org.postgresql.postgresql"); }

    }

    public static class OrgProjectlombokVersionAccessors extends VersionFactory  {

        public OrgProjectlombokVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.projectlombok.lombok</b> with value <b>1.18.42</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLombok() { return getVersion("org.projectlombok.lombok"); }

    }

    public static class OrgSpringdocVersionAccessors extends VersionFactory  {

        private final OrgSpringdocSpringdocVersionAccessors vaccForOrgSpringdocSpringdocVersionAccessors = new OrgSpringdocSpringdocVersionAccessors(providers, config);
        public OrgSpringdocVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springdoc.springdoc</b>
         */
        public OrgSpringdocSpringdocVersionAccessors getSpringdoc() {
            return vaccForOrgSpringdocSpringdocVersionAccessors;
        }

    }

    public static class OrgSpringdocSpringdocVersionAccessors extends VersionFactory  {

        private final OrgSpringdocSpringdocOpenapiVersionAccessors vaccForOrgSpringdocSpringdocOpenapiVersionAccessors = new OrgSpringdocSpringdocOpenapiVersionAccessors(providers, config);
        public OrgSpringdocSpringdocVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springdoc.springdoc.openapi</b>
         */
        public OrgSpringdocSpringdocOpenapiVersionAccessors getOpenapi() {
            return vaccForOrgSpringdocSpringdocOpenapiVersionAccessors;
        }

    }

    public static class OrgSpringdocSpringdocOpenapiVersionAccessors extends VersionFactory  {

        private final OrgSpringdocSpringdocOpenapiStarterVersionAccessors vaccForOrgSpringdocSpringdocOpenapiStarterVersionAccessors = new OrgSpringdocSpringdocOpenapiStarterVersionAccessors(providers, config);
        public OrgSpringdocSpringdocOpenapiVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springdoc.springdoc.openapi.starter</b>
         */
        public OrgSpringdocSpringdocOpenapiStarterVersionAccessors getStarter() {
            return vaccForOrgSpringdocSpringdocOpenapiStarterVersionAccessors;
        }

    }

    public static class OrgSpringdocSpringdocOpenapiStarterVersionAccessors extends VersionFactory  {

        private final OrgSpringdocSpringdocOpenapiStarterWebmvcVersionAccessors vaccForOrgSpringdocSpringdocOpenapiStarterWebmvcVersionAccessors = new OrgSpringdocSpringdocOpenapiStarterWebmvcVersionAccessors(providers, config);
        public OrgSpringdocSpringdocOpenapiStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springdoc.springdoc.openapi.starter.webmvc</b>
         */
        public OrgSpringdocSpringdocOpenapiStarterWebmvcVersionAccessors getWebmvc() {
            return vaccForOrgSpringdocSpringdocOpenapiStarterWebmvcVersionAccessors;
        }

    }

    public static class OrgSpringdocSpringdocOpenapiStarterWebmvcVersionAccessors extends VersionFactory  {

        public OrgSpringdocSpringdocOpenapiStarterWebmvcVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springdoc.springdoc.openapi.starter.webmvc.ui</b> with value <b>2.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getUi() { return getVersion("org.springdoc.springdoc.openapi.starter.webmvc.ui"); }

    }

    public static class OrgSpringframeworkVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootVersionAccessors vaccForOrgSpringframeworkBootVersionAccessors = new OrgSpringframeworkBootVersionAccessors(providers, config);
        private final OrgSpringframeworkCloudVersionAccessors vaccForOrgSpringframeworkCloudVersionAccessors = new OrgSpringframeworkCloudVersionAccessors(providers, config);
        private final OrgSpringframeworkKafkaVersionAccessors vaccForOrgSpringframeworkKafkaVersionAccessors = new OrgSpringframeworkKafkaVersionAccessors(providers, config);
        private final OrgSpringframeworkSecurityVersionAccessors vaccForOrgSpringframeworkSecurityVersionAccessors = new OrgSpringframeworkSecurityVersionAccessors(providers, config);
        public OrgSpringframeworkVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot</b>
         */
        public OrgSpringframeworkBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework.cloud</b>
         */
        public OrgSpringframeworkCloudVersionAccessors getCloud() {
            return vaccForOrgSpringframeworkCloudVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework.kafka</b>
         */
        public OrgSpringframeworkKafkaVersionAccessors getKafka() {
            return vaccForOrgSpringframeworkKafkaVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework.security</b>
         */
        public OrgSpringframeworkSecurityVersionAccessors getSecurity() {
            return vaccForOrgSpringframeworkSecurityVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringVersionAccessors vaccForOrgSpringframeworkBootSpringVersionAccessors = new OrgSpringframeworkBootSpringVersionAccessors(providers, config);
        public OrgSpringframeworkBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring</b>
         */
        public OrgSpringframeworkBootSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkBootSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootVersionAccessors vaccForOrgSpringframeworkBootSpringBootVersionAccessors = new OrgSpringframeworkBootSpringBootVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot</b>
         */
        public OrgSpringframeworkBootSpringBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootSpringBootVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootDockerVersionAccessors vaccForOrgSpringframeworkBootSpringBootDockerVersionAccessors = new OrgSpringframeworkBootSpringBootDockerVersionAccessors(providers, config);
        private final OrgSpringframeworkBootSpringBootStarterVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors = new OrgSpringframeworkBootSpringBootStarterVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.docker</b>
         */
        public OrgSpringframeworkBootSpringBootDockerVersionAccessors getDocker() {
            return vaccForOrgSpringframeworkBootSpringBootDockerVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.starter</b>
         */
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors getStarter() {
            return vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootDockerVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkBootSpringBootDockerVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.docker.compose</b> with value <b>3.5.8</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getCompose() { return getVersion("org.springframework.boot.spring.boot.docker.compose"); }

    }

    public static class OrgSpringframeworkBootSpringBootStarterVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootStarterDataVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterDataVersionAccessors = new OrgSpringframeworkBootSpringBootStarterDataVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.security</b> with value <b>3.5.8</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSecurity() { return getVersion("org.springframework.boot.spring.boot.starter.security"); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.test</b> with value <b>3.5.8</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTest() { return getVersion("org.springframework.boot.spring.boot.starter.test"); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.web</b> with value <b>3.5.8</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getWeb() { return getVersion("org.springframework.boot.spring.boot.starter.web"); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.starter.data</b>
         */
        public OrgSpringframeworkBootSpringBootStarterDataVersionAccessors getData() {
            return vaccForOrgSpringframeworkBootSpringBootStarterDataVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterDataVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkBootSpringBootStarterDataVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.data.jpa</b> with value <b>3.5.8</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJpa() { return getVersion("org.springframework.boot.spring.boot.starter.data.jpa"); }

    }

    public static class OrgSpringframeworkCloudVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringVersionAccessors vaccForOrgSpringframeworkCloudSpringVersionAccessors = new OrgSpringframeworkCloudSpringVersionAccessors(providers, config);
        public OrgSpringframeworkCloudVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring</b>
         */
        public OrgSpringframeworkCloudSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkCloudSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudVersionAccessors = new OrgSpringframeworkCloudSpringCloudVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud</b>
         */
        public OrgSpringframeworkCloudSpringCloudVersionAccessors getCloud() {
            return vaccForOrgSpringframeworkCloudSpringCloudVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterVersionAccessors getStarter() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.cloud.spring.cloud.starter.bootstrap</b> with value <b>4.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBootstrap() { return getVersion("org.springframework.cloud.spring.cloud.starter.bootstrap"); }

        /**
         * Version alias <b>org.springframework.cloud.spring.cloud.starter.config</b> with value <b>4.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getConfig() { return getVersion("org.springframework.cloud.spring.cloud.starter.config"); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter.netflix</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors getNetflix() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter.netflix.eureka</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors getEureka() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka.client</b> with value <b>4.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getClient() { return getVersion("org.springframework.cloud.spring.cloud.starter.netflix.eureka.client"); }

    }

    public static class OrgSpringframeworkKafkaVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkKafkaSpringVersionAccessors vaccForOrgSpringframeworkKafkaSpringVersionAccessors = new OrgSpringframeworkKafkaSpringVersionAccessors(providers, config);
        public OrgSpringframeworkKafkaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.kafka.spring</b>
         */
        public OrgSpringframeworkKafkaSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkKafkaSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkKafkaSpringVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkKafkaSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.kafka.spring.kafka</b> with value <b>3.3.11</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKafka() { return getVersion("org.springframework.kafka.spring.kafka"); }

    }

    public static class OrgSpringframeworkSecurityVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkSecuritySpringVersionAccessors vaccForOrgSpringframeworkSecuritySpringVersionAccessors = new OrgSpringframeworkSecuritySpringVersionAccessors(providers, config);
        public OrgSpringframeworkSecurityVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.security.spring</b>
         */
        public OrgSpringframeworkSecuritySpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkSecuritySpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkSecuritySpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkSecuritySpringSecurityVersionAccessors vaccForOrgSpringframeworkSecuritySpringSecurityVersionAccessors = new OrgSpringframeworkSecuritySpringSecurityVersionAccessors(providers, config);
        public OrgSpringframeworkSecuritySpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.security.spring.security</b>
         */
        public OrgSpringframeworkSecuritySpringSecurityVersionAccessors getSecurity() {
            return vaccForOrgSpringframeworkSecuritySpringSecurityVersionAccessors;
        }

    }

    public static class OrgSpringframeworkSecuritySpringSecurityVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkSecuritySpringSecurityVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.security.spring.security.test</b> with value <b>6.5.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTest() { return getVersion("org.springframework.security.spring.security.test"); }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
