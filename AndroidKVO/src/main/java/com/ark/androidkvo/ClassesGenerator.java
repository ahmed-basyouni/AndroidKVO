/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Ahmed basyouni
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ark.androidkvo;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

/**
 * Here is where all the magic happen we pass the class here and take care of all the imports
 * extending the original class make an enum with all fields that we need and overwriting the setter
 * to make an interception of changing the value and notify the listener with the new value
 *
 * Created by ahmed-basyouni on 12/31/16.
 */

class ClassesGenerator {

    static StringBuilder generateClass(AnnotatedClass annotatedClass) {

        StringBuilder builder = new StringBuilder()
                .append("/**\n" +
                        " * The MIT License (MIT)\n" +
                        " * Copyright (c) 2016 Ahmed basyouni\n" +
                        " * <p>\n" +
                        " * Permission is hereby granted, free of charge, to any person obtaining a copy of this software\n" +
                        " * and associated documentation files (the \"Software\"), to deal in the Software without restriction,\n" +
                        " * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,\n" +
                        " * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,\n" +
                        " * subject to the following conditions:\n" +
                        " * The above copyright notice and this permission notice shall be included in all copies or substantial portions\n" +
                        " * of the Software.\n" +
                        " * THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,\n" +
                        " * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND\n" +
                        " * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,\n" +
                        " * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                        " * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\n" +
                        " */\n").append("package ").append(annotatedClass.packageName).append(".kvo;\n\n")
                .append("import com.ark.androidkvo.KVOListener;\n")
                .append("import com.ark.androidkvo.KVOManager;\n")
                .append("import java.io.Serializable;\n")
                .append("import android.app.Activity;\n")
                .append("import android.support.v4.app.Fragment;\n")
                .append("import com.ark.androidkvo.KVOField;\n")
                .append("import java.lang.reflect.Field;\n")
                .append("import java.lang.ref.WeakReference;\n")
                .append("import java.util.List;")
                .append("import java.util.Iterator;\n")
                .append("import java.util.ArrayList;\n")
                .append("import com.ark.androidkvo.FieldObject;\n")
                .append("import com.ark.androidkvo.KVOObserverObject;\n").append("import ").append(annotatedClass.packageName).append(".").append(annotatedClass.annotatedClass.getSimpleName()).append(";\n");

                builder.append("public final class ").append(annotatedClass.annotatedClass.getSimpleName()).append("KVO extends ").append(annotatedClass.annotatedClass.getSimpleName()).append(" implements Serializable").append("{\n\n") // open class
                .append("   ArrayList<FieldObject> allKVOFields = new ArrayList<FieldObject>() {{\n");
        for (VariableElement field : annotatedClass.annotatedFields) {
            builder.append("        add(new FieldObject(\"").append(field.getSimpleName()).append("\"").append(",").append("\"").append(field.getAnnotation(KVOField.class).id()).append("\"").append("));\n");
        }
        builder.append("   }};\n")
                .append("   public enum FieldName{\n")
                .append("       ");
        for(int x = 0 ; x < annotatedClass.annotatedFields.size() ; x++){
            VariableElement field = annotatedClass.annotatedFields.get(x);
            builder.append(field.getSimpleName());
            if(x != annotatedClass.annotatedFields.size() -1){
                builder.append(",");
            }else {
                builder.append("\n   }\n\n");
            }
        }

        for (ExecutableElement cons :
                ElementFilter.constructorsIn(annotatedClass.annotatedClass.getEnclosedElements())) {

                TypeElement declaringClass =
                        (TypeElement) cons.getEnclosingElement();
                builder.append("    public ").append(declaringClass.getSimpleName().toString()).append("KVO(");
                for(int x = 0 ; x < cons.getParameters().size() ; x++){
                    if(x > 0)
                        builder.append(",");
                    VariableElement variableElement = cons.getParameters().get(x);
                    builder.append(variableElement.asType().toString()).append(" param").append(x);
                }
                builder.append("){\n");
                builder.append("        super(");
                for(int x = 0 ; x < cons.getParameters().size() ; x++){
                    if(x > 0)
                        builder.append(",");
                    builder.append("param").append(x);
                }
                builder.append(");\n");
            builder.append("}\n");
        }

        builder.append("    /**\n" +
                "     * you can use this method to set a callback for a certain field \n" +
                "     * All You have to do is pass object that implement {@link KVOListener} and pass the field name using {@link FieldName}\n" +
                "     * Which you can find inside the generated class you can access it like this generatedClass.FieldName.Field where generated class is your className+KVO\n" +
                "     * and Field is your field name the purpose of that in case you change the field name you got a compilation error instead of searching for fields name\n" +
                "     * as strings\n" +
                "     * \n" +
                "     * @param listener\n" +
                "     * @param property\n" +
                "     */\n");
        builder.append("   public void setListener(KVOListener listener , FieldName property){\n").append("       boolean fieldExist = false;\n" + "     Field[] fields = ").append(annotatedClass.annotatedClass.getSimpleName()).append(".class.getFields();\n").append("      String fieldId = null;\n").append("     for(Field field : fields){\n").append("         if(property.name().equals(field.getName())){\n").append("             if(field.getAnnotation(KVOField.class) == null)\n").append("                 throw new RuntimeException(\"Field \"+ field.getName() +\" must be annotated with KVOField\");\n").append("             fieldExist = true;\n").append("              fieldId = field.getAnnotation(KVOField.class).id();\n").append("             break;\n").append("         }\n").append("     }\n").append("     if(!fieldExist)\n").append("         throw new RuntimeException(\"Field with name \" + property.name() + \" does not exist or it maybe private\");\n").append("     KVOObserverObject observerObject = new KVOObserverObject();\n").append("     observerObject.setListener(listener);\n").append("      observerObject.setFieldId(fieldId);\n").append("     observerObject.setPropertyName(property.name());\n").append("           if(!fieldId.equals(\"\")){\n").append("               KVOManager.getInstance().addIdentifiedObserver(fieldId, listener);\n").append("           }else {\n").append("               if (!KVOManager.getInstance().getObservers().contains(observerObject)) {\n").append("                   KVOManager.getInstance().addObserver(observerObject);\n").append("               }\n").append("           }\n").append("   }\n");

        builder.append("    /**\n" +
                "     * you can use this method to listen for all the changes that happen on all fields that is annotated by {@link KVOField}\n" +
                "     * if you need a certain field instead use {@link #setKvo(KVOListener, FieldName)}\n" +
                "     * @param listener\n" +
                "     */\n");
        builder.append("    public void setListener(KVOListener listener){\n" +
                "       for(FieldObject field : allKVOFields){\n" +
                "           KVOObserverObject observerObject = new KVOObserverObject();\n" +
                "           observerObject.setListener(listener);\n" +
                "           observerObject.setPropertyName(field.getFieldName());\n" +
                "           observerObject.setFieldId(field.getFieldID());\n"+
                "           if(!field.getFieldID().equals(\"\")){\n" +
                "               KVOManager.getInstance().addIdentifiedObserver(field.getFieldID(), listener);\n" +
                "           }else {\n" +
                "               if (!KVOManager.getInstance().getObservers().contains(observerObject)) {\n" +
                "                   KVOManager.getInstance().addObserver(observerObject);\n" +
                "               }\n" +
                "           }\n"+
                "        }\n" +
                "   }\n\n");

        builder.append("    /**\n" +
                "     * use this method to remove the callback listener \n" +
                "     * @param kvoListener\n" +
                "     */\n");
        builder.append("    public void removeListener(KVOListener kvoListener){\n" +
                "\n" +
                "        for (Iterator<KVOObserverObject> iterator = KVOManager.getInstance().getObservers().iterator(); iterator.hasNext();) {\n" +
                "            KVOObserverObject observerObject = iterator.next();\n" +
                "            if (observerObject.getListener().equals(kvoListener)) {\n" +
                "                // Remove the current element from the iterator and the list.\n" +
                "                iterator.remove();\n" +
                "            }\n" +
                "        }\n" +
                "           KVOManager.getInstance().removeIdentifiedObserver(kvoListener);\n"+
                "    }\n\n");

        builder.append("    /**\n" +
                "     * you can use this method to listen for all the changes that happen to a certain field annotated with certain ID\n" +
                "     * {@link KVOField#id()}\n" +
                "     * please note that all the fields annoteted with same ID will trigger the listener so make sure that the id is unique\n" +
                "     * @param listener\n" +
                "     * @param id\n" +
                "     */\n");

        builder.append("    public void setListenerForId(KVOListener listener, String id) {\n" + "\n" + "        boolean fieldExist = false;\n" + "        Field[] fields = ").append(annotatedClass.annotatedClass.getSimpleName()).append(".class.getFields();\n").append("        for (Field field : fields) {\n").append("            if (field.getAnnotation(KVOField.class) == null)\n").append("                throw new RuntimeException(\"Field \" + field.getName() + \" must be annotated with KVOField\");\n").append("            else if(field.getAnnotation(KVOField.class).id().equals(id)) {\n").append("                fieldExist = true;\n").append("                break;\n").append("            }\n").append("        }\n").append("        if (!fieldExist)\n").append("            throw new RuntimeException(\"Field with id \" + id + \" does not exist or it maybe private\");\n").append("        KVOManager.getInstance().addIdentifiedObserver(id , listener);\n").append("    }\n");

        for (VariableElement field : annotatedClass.annotatedFields) {

            builder.append("    public void set").append(capitalize(field.getSimpleName().toString())).append("(").append(field.asType().toString()).append(" param)").append(" {\n")
                    .append("        KVOObserverObject observerObject = initKVOProcess();\n")
                    .append("        if (observerObject != null && observerObject.getListener() != null) {\n" +
                            "            observerObject.getListener().onValueChange(this, param, observerObject.getPropertyName());\n" +
                            "        } else if (observerObject != null && observerObject.getListener() == null){\n")
                    .append("            KVOManager.getInstance().removeObserver(observerObject);\n")
                    .append("        } else {\n" +
                            "            checkIdInManager(param);\n" +
                            "        }\n").append("        this.").append(field.getSimpleName()).append(" = param;\n")
                    .append("    }\n\n");
        }

        builder.append("    /**\n" +
                "     * class use this method to try to find an observer which is registered to this param\n" +
                "     * if it found one it will notify it that the value has changed\n" +
                "     * @param param\n" +
                "     */\n");

        builder.append("    private void checkIdInManager(Object param){\n" +
                "               for (FieldObject field : allKVOFields) {\n" +
                        "            if (field.getFieldName().equalsIgnoreCase(getFieldName())) {\n" +
                        "                if (!field.getFieldID().equals(\"\")) {\n" +
                        "                    List<KVOListener> listeners = getListenerForId(field.getFieldID());\n" +
                        "                    if (listeners != null) {\n" +
                        "                        for (KVOListener listener : listeners) {\n" +
                        "                            listener.onValueChange(this,param,field.getFieldID());\n" +
                        "                        }\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            }\n" +
                        "\n" +
                        "        }\n"+
                "    }\n");

        builder.append("    /**\n" +
                "     * this method get the caller method which is by default the setter method\n" +
                "     * get the name of method so we can get the variable name from that\n" +
                "     * and then call {@link #containProperty(String)}\n" +
                "     * @return\n" +
                "     */\n");

        builder.append("    private List<KVOListener> getListenerForId(String id) {\n" +
                "        List<KVOListener> targetList = new ArrayList<>();\n" +
                "        List<WeakReference<KVOListener>> sourceList = KVOManager.getInstance().getIdentifiedObservers().get(id);\n" +
                "        if (sourceList != null && !sourceList.isEmpty()) {\n" +
                "            for (Iterator<WeakReference<KVOListener>> iterator = sourceList.iterator(); iterator.hasNext(); ) {\n" +
                "                KVOListener observerObject = iterator.next().get();\n" +
                "                if (observerObject != null) {\n" +
                "                    if (observerObject instanceof Activity)\n" +
                "                        if (((Activity) observerObject).isFinishing()) {\n" +
                "                            iterator.remove();\n" +
                "                            return null;\n" +
                "                        }\n" +
                "                    if (observerObject instanceof Fragment)\n" +
                "                        if (((Fragment) observerObject).getActivity().isFinishing()) {\n" +
                "                            iterator.remove();\n" +
                "                            return null;\n" +
                "                        }\n" +
                "                    if (observerObject instanceof android.app.Fragment)\n" +
                "                        if (((android.app.Fragment) observerObject).getActivity().isFinishing()) {\n" +
                "                            iterator.remove();\n" +
                "                            return null;\n" +
                "                        }\n" +
                "                    targetList.add(observerObject);\n" +
                "                } else if (observerObject == null) {\n" +
                "                    iterator.remove();\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        return targetList;\n" +
                "    }\n");

        builder.append("    private KVOObserverObject initKVOProcess(){\n" +
                "       String fieldName = getFieldName();\n" +
                "\n" +
                "       return containProperty(fieldName);\n" +
                "    }\n" +
                "\n" +
                "      private String getFieldName() {\n" +
                        "        String methodName = Thread.currentThread().getStackTrace()[4].getMethodName();\n" +
                        "        return methodName.substring(3);\n" +
                        "    }\n"+
                "   /**\n" +
                        "     * this method iterate through all the observers and if observer is assigned to field it return that observer in case\n" +
                        "     * it's not null since we use weak refrence see {@link KVOObserverObject#listener}\n" +
                        "     * and if it's not null but it's an activity or fragment we need to check if activity is finished or not\n" +
                        "     * the fragment or activity maybe finished but the GC not yet removed it from memory so weak reference would still got value and not null\n" +
                        "     * @param propertyName\n" +
                        "     * @return\n" +
                        "     */\n"+
                "    private KVOObserverObject containProperty(String propertyName){\n" +
                "\n" +
                "       for (Iterator<KVOObserverObject> iterator = KVOManager.getInstance().getObservers().iterator(); iterator.hasNext(); ) {\n" +
                "           KVOObserverObject observerObject = iterator.next();\n" +
                "           if (observerObject.getPropertyName().equalsIgnoreCase(propertyName) && observerObject.getListener() != null) {\n"+
                "               if(observerObject.getListener() instanceof Activity)\n"+
                "                   if(((Activity)observerObject.getListener()).isFinishing()){\n"+
                "                       iterator.remove();\n"+
                "                       return null;\n"+
                "                   }\n"+
                "               if(observerObject.getListener() instanceof Fragment)\n"+
                "                   if(((Fragment)observerObject.getListener()).getActivity().isFinishing()){\n"+
                "                       iterator.remove();\n"+
                "                       return null;\n"+
                "                   }\n"+
                "               if(observerObject.getListener() instanceof android.app.Fragment)\n"+
                "                   if(((android.app.Fragment)observerObject.getListener()).getActivity().isFinishing()){\n"+
                "                       iterator.remove();\n"+
                "                       return null;\n"+
                "                   }\n"+
                "               return observerObject;\n" +
                "           }else if(observerObject.getListener() == null){\n" +
                "               iterator.remove();\n"+
                "           }\n"+
                "       }\n"+
                "        return null;\n" +
                "    }\n")
                .append("}\n"); // close class

        return builder;
    }

    private static String capitalize(String s) {
        if (s == null)
            return null;
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        if (s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        return "";
    }
}
