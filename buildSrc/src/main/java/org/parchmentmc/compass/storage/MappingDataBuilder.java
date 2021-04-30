package org.parchmentmc.compass.storage;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A mutable builder implementation of {@link MappingDataContainer}.
 */
public class MappingDataBuilder implements MappingDataContainer {
    private final Set<MutablePackageData> packages = new TreeSet<>(PackageData.COMPARATOR);
    private transient final Map<String, MutablePackageData> packagesMap = new HashMap<>();
    private transient final Collection<MutablePackageData> packagesView = Collections.unmodifiableSet(packages);
    private final Set<MutableClassData> classes = new TreeSet<>(ClassData.COMPARATOR);
    private transient final Map<String, MutableClassData> classesMap = new HashMap<>();
    private transient final Collection<MutableClassData> classesView = Collections.unmodifiableSet(classes);

    public MappingDataBuilder() {
    }

    @Override
    public Collection<MutablePackageData> getPackages() {
        return packagesView;
    }

    @Nullable
    @Override
    public MutablePackageData getPackage(String packageName) {
        return packagesMap.get(packageName);
    }

    public MutablePackageData addPackage(String packageName) {
        MutablePackageData pkg = new MutablePackageData(packageName);
        packages.add(pkg);
        packagesMap.put(packageName, pkg);
        return pkg;
    }

    @Override
    public Collection<? extends MutableClassData> getClasses() {
        return classesView;
    }

    @Nullable
    @Override
    public MutableClassData getClass(String className) {
        return classesMap.get(className);
    }

    public MutableClassData addClass(String className) {
        MutableClassData cls = new MutableClassData(className);
        classes.add(cls);
        classesMap.put(className, cls);
        return cls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MappingDataContainer)) return false;
        MappingDataContainer builder = (MappingDataContainer) o;
        return getPackages().equals(builder.getPackages()) && getClasses().equals(builder.getClasses());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPackages(), getClasses());
    }

    public static class MutablePackageData implements MappingDataContainer.PackageData {
        private final String name;
        private final List<String> javadoc = new ArrayList<>();
        private transient final List<String> javadocView = Collections.unmodifiableList(javadoc);

        MutablePackageData(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getJavadoc() {
            return javadocView;
        }

        public MutablePackageData addJavadoc(String... line) {
            return addJavadoc(Arrays.asList(line));
        }

        public MutablePackageData addJavadoc(Collection<? extends String> lines) {
            javadoc.addAll(lines);
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PackageData)) return false;
            PackageData that = (PackageData) o;
            return Objects.equals(getName(), that.getName()) && getJavadoc().equals(that.getJavadoc());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getJavadoc());
        }
    }

    public static class MutableClassData implements MappingDataContainer.ClassData {
        private final String name;
        private final List<String> javadoc = new ArrayList<>();
        private transient final List<String> javadocView = Collections.unmodifiableList(javadoc);

        private final Set<MutableFieldData> fields = new TreeSet<>(FieldData.COMPARATOR);
        private transient final Map<String, MutableFieldData> fieldsMap = new HashMap<>();
        private transient final Collection<MutableFieldData> fieldsView = Collections.unmodifiableSet(fields);

        // Keys for method map is '<name>:<descriptor>'
        private final Set<MutableMethodData> methods = new TreeSet<>(MethodData.COMPARATOR);
        private transient final Map<String, MutableMethodData> methodsMap = new HashMap<>();
        private transient final Collection<MutableMethodData> methodsView = Collections.unmodifiableSet(methods);

        MutableClassData(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getJavadoc() {
            return javadocView;
        }

        public MutableClassData addJavadoc(String... line) {
            return addJavadoc(Arrays.asList(line));
        }

        public MutableClassData addJavadoc(Collection<? extends String> lines) {
            javadoc.addAll(lines);
            return this;
        }

        public MutableClassData clearJavadoc() {
            javadoc.clear();
            return this;
        }

        @Override
        public Collection<MutableFieldData> getFields() {
            return fieldsView;
        }

        @Nullable
        @Override
        public MutableFieldData getField(String fieldName) {
            return fieldsMap.get(fieldName);
        }

        public MutableFieldData addField(String fieldName) {
            MutableFieldData field = new MutableFieldData(fieldName);
            fields.add(field);
            fieldsMap.put(fieldName, field);
            return field;
        }

        @Override
        public Collection<MutableMethodData> getMethods() {
            return methodsView;
        }

        @Nullable
        @Override
        public MutableMethodData getMethod(String methodName, String descriptor) {
            return methodsMap.get(key(methodName, descriptor));
        }

        public MutableMethodData addMethod(String methodName, String descriptor) {
            MutableMethodData method = new MutableMethodData(methodName, descriptor);
            methods.add(method);
            methodsMap.put(key(methodName, descriptor), method);
            return method;
        }

        private String key(String methodName, String descriptor) {
            return methodName + ":" + descriptor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClassData)) return false;
            ClassData that = (ClassData) o;
            return Objects.equals(getName(), that.getName()) && getJavadoc().equals(that.getJavadoc())
                    && getFields().equals(that.getFields()) && getMethods().equals(that.getMethods());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getJavadoc(), getFields(), getMethods());
        }
    }

    public static class MutableFieldData implements MappingDataContainer.FieldData {
        private final String name;
        private String descriptor;
        private final List<String> javadoc = new ArrayList<>();
        private transient final List<String> javadocView = Collections.unmodifiableList(javadoc);

        MutableFieldData(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescriptor() {
            if (descriptor == null) throw new IllegalStateException("Field descriptor is not set");
            return descriptor;
        }

        public MutableFieldData setDescriptor(String descriptor) {
            this.descriptor = descriptor;
            return this;
        }

        @Override
        public List<String> getJavadoc() {
            return javadocView;
        }

        public MutableFieldData addJavadoc(String... line) {
            return addJavadoc(Arrays.asList(line));
        }

        public MutableFieldData addJavadoc(Collection<? extends String> lines) {
            javadoc.addAll(lines);
            return this;
        }

        public MutableFieldData clearJavadoc() {
            javadoc.clear();
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FieldData)) return false;
            FieldData that = (FieldData) o;
            return getName().equals(that.getName()) && Objects.equals(getDescriptor(), that.getDescriptor())
                    && getJavadoc().equals(that.getJavadoc());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getDescriptor(), getJavadoc());
        }
    }

    public static class MutableMethodData implements MappingDataContainer.MethodData {
        private final String name;
        private final String descriptor;
        private final List<String> javadoc = new ArrayList<>();
        private transient final List<String> javadocView = Collections.unmodifiableList(javadoc);
        private final Set<MutableParameterData> parameters = new TreeSet<>(ParameterData.COMPARATOR);
        private transient final Map<Byte, MutableParameterData> parametersMap = new HashMap<>();
        private transient final Collection<MutableParameterData> parametersView = Collections.unmodifiableSet(parameters);

        MutableMethodData(String name, String descriptor) {
            this.name = name;
            this.descriptor = descriptor;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescriptor() {
            return descriptor;
        }

        @Override
        public List<String> getJavadoc() {
            return javadocView;
        }

        public MutableMethodData addJavadoc(String... line) {
            return addJavadoc(Arrays.asList(line));
        }

        public MutableMethodData addJavadoc(Collection<? extends String> lines) {
            javadoc.addAll(lines);
            return this;
        }

        public MutableMethodData clearJavadoc() {
            javadoc.clear();
            return this;
        }

        @Override
        public Collection<? extends MappingDataContainer.ParameterData> getParameters() {
            return parametersView;
        }

        @Nullable
        @Override
        public MappingDataContainer.ParameterData getParameter(byte index) {
            return parametersMap.get(index);
        }

        public MutableParameterData addParameter(byte index) {
            MutableParameterData param = new MutableParameterData(index);
            parameters.add(param);
            parametersMap.put(index, param);
            return param;
        }

        public MutableMethodData clearParameters() {
            parameters.clear();
            parametersMap.clear();
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodData)) return false;
            MethodData that = (MethodData) o;
            return getName().equals(that.getName()) && getDescriptor().equals(that.getDescriptor())
                    && getJavadoc().equals(that.getJavadoc()) && getParameters().equals(that.getParameters());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getDescriptor(), getJavadoc(), getParameters());
        }
    }

    public static class MutableParameterData implements MappingDataContainer.ParameterData {
        private final byte index;
        @Nullable
        private String name = null;
        @Nullable
        private String javadoc = null;

        MutableParameterData(byte index) {
            this.index = index;
        }

        @Override
        public byte getIndex() {
            return index;
        }

        @Nullable
        @Override
        public String getName() {
            return name;
        }

        public MutableParameterData setName(@Nullable String name) {
            this.name = name;
            return this;
        }

        @Nullable
        @Override
        public String getJavadoc() {
            return javadoc;
        }

        public MutableParameterData setJavadoc(@Nullable String javadoc) {
            this.javadoc = javadoc;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ParameterData)) return false;
            ParameterData that = (ParameterData) o;
            return getIndex() == that.getIndex() && Objects.equals(getName(), that.getName()) && Objects.equals(getJavadoc(), that.getJavadoc());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getIndex(), getName(), getJavadoc());
        }
    }
}
