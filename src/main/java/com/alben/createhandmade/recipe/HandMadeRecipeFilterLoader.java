package com.alben.createhandmade.recipe;

import com.alben.createhandmade.CreateHandMade;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

/**
 * 配方过滤层 L2 的数据包加载器。
 *
 * <p>读取 {@code data/<namespace>/create_hand_made/tool_filter/<tool_id>.json}，
 * 把内容灌进 {@link HandMadeRecipeFilters}。</p>
 *
 * <p><b>为什么用 {@link SimpleJsonResourceReloadListener} 而不是手写
 * {@code PreparableReloadListener} + {@code ResourceManager.listResources}：</b></p>
 * <ol>
 *   <li>与项目现有风格一致（见 {@code BellowsMediaReloadListener}）；</li>
 *   <li><b>覆盖语义正确</b>：{@code listResources} 会把所有数据包里同名的文件
 *       <b>全部</b>列出来（包括被高优先级包覆盖掉的那一份），自己处理覆盖容易出错；
 *       本类走原版的 {@code FileToIdConverter}，同名文件只保留优先级最高的那一份。</li>
 * </ol>
 *
 * <p>map 的 key 是 {@code <包namespace>:<tool_id>}，所以 {@code getPath()} 正好就是
 * {@code <tool_id>}（目录前缀与 {@code .json} 后缀都由原版剥掉了）。</p>
 *
 * <p>解析失败一律「记日志 + 跳过这一条」，不让整个 reload 失败。</p>
 */
public class HandMadeRecipeFilterLoader extends SimpleJsonResourceReloadListener {

    /** 数据包目录：{@code data/<namespace>/create_hand_made/tool_filter/}。 */
    public static final String DIRECTORY = "create_hand_made/tool_filter";

    private static final String FIELD_DISABLED = "disabled";
    private static final String FIELD_DISABLED_BY_MOD = "disabled_by_mod";

    public HandMadeRecipeFilterLoader() {
        super(new Gson(), DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries,
                         ResourceManager manager, ProfilerFiller profiler) {
        // 先清空：重载后旧的过滤项必须消失（例如数据包把某个文件删掉了）
        HandMadeRecipeFilters.clearDataPack();

        int applied = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : entries.entrySet()) {
            // key = <包namespace>:<tool_id>，path 就是 tool_id
            String toolId = entry.getKey().getPath();

            HandMadeTool tool = parseTool(toolId, entry.getKey());
            if (tool == null) continue;

            try {
                JsonElement root = entry.getValue();
                if (root == null || !root.isJsonObject()) {
                    CreateHandMade.LOGGER.warn(
                            "Tool filter {} must be a JSON object — skipping", entry.getKey());
                    continue;
                }

                JsonObject object = root.getAsJsonObject();
                readDisabled(object, tool);
                readDisabledByMod(object, tool);
                applied++;
            } catch (Exception ex) {
                // 与 BellowsMediaReloadListener 同样的风格：单条失败不影响整体 reload
                CreateHandMade.LOGGER.warn(
                        "Failed to parse tool filter {} — skipping", entry.getKey(), ex);
            }
        }

        if (!entries.isEmpty()) {
            CreateHandMade.LOGGER.info(
                    "Loaded {} tool filter file(s) for create_hand_made", applied);
        }
    }

    /**
     * 把 {@code <tool_id>} 转成 {@link HandMadeTool}。
     *
     * <p>{@link HandMadeTool} 没有 id 字段，所以直接用枚举名：
     * {@code hand_press_basin} → {@code HAND_PRESS_BASIN}。</p>
     *
     * @return 对应的枚举值；无法识别时返回 null（已记 warning）
     */
    @Nullable
    private static HandMadeTool parseTool(String toolId, ResourceLocation fileId) {
        try {
            return HandMadeTool.valueOf(toolId.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            CreateHandMade.LOGGER.warn(
                    "Unknown tool id '{}' in tool filter {} — skipping. Valid ids: {}",
                    toolId, fileId, validToolIds());
            return null;
        }
    }

    /** 读取 {@code disabled}：精确 recipe id 列表。 */
    private static void readDisabled(JsonObject root, HandMadeTool tool) {
        JsonArray ids = arrayField(root, FIELD_DISABLED);
        if (ids == null) return;

        for (JsonElement element : ids) {
            String raw = element.getAsString();
            ResourceLocation recipeId = ResourceLocation.tryParse(raw);
            if (recipeId == null) {
                CreateHandMade.LOGGER.warn(
                        "Invalid recipe id '{}' in '{}' for tool {} — skipping entry",
                        raw, FIELD_DISABLED, tool);
                continue;
            }
            HandMadeRecipeFilters.disableFromDataPack(tool, recipeId);
        }
    }

    /** 读取 {@code disabled_by_mod}：namespace 列表。 */
    private static void readDisabledByMod(JsonObject root, HandMadeTool tool) {
        JsonArray mods = arrayField(root, FIELD_DISABLED_BY_MOD);
        if (mods == null) return;

        for (JsonElement element : mods) {
            HandMadeRecipeFilters.disableByModFromDataPack(tool, element.getAsString());
        }
    }

    /**
     * 取一个「字符串数组」字段。
     *
     * @return 缺省 / 为 null 时返回 null；类型不对时记 warning 并返回 null
     */
    @Nullable
    private static JsonArray arrayField(JsonObject root, String field) {
        if (!root.has(field)) {
            return null;   // 缺省视为空
        }

        JsonElement element = root.get(field);
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (!element.isJsonArray()) {
            CreateHandMade.LOGGER.warn("Field '{}' must be a string array — ignoring", field);
            return null;
        }
        return element.getAsJsonArray();
    }

    /**
     * 列出所有合法的 {@code tool_id}，逗号分隔。
     *
     * <p>数据包加载器用它写 warning 日志；KubeJS 侧
     * （{@code HandMadeToolFilterKubeEvent}）用它拼异常消息 ——
     * 两边共用同一份清单，避免枚举新增时漏改一处。</p>
     */
    public static String validToolIds() {
        StringBuilder sb = new StringBuilder();
        for (HandMadeTool tool : HandMadeTool.values()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(tool.name().toLowerCase(Locale.ROOT));
        }
        return sb.toString();
    }
}
