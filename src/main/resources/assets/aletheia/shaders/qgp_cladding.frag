#version 120

uniform sampler2D texture;
uniform sampler2D u_screenTexture;
uniform float iTime;
uniform float u_screenWidth;
uniform float u_screenHeight;
uniform int u_hasFbo;
uniform vec4 u_iconUvRange;

float hexDist(vec2 p) {
    p = abs(p);
    float c = dot(p, normalize(vec2(1.0, 1.7320508)));
    c = max(c, p.x);
    return c;
}

vec4 hexGrid(vec2 p) {
    vec2 r = vec2(1.0, 1.7320508);
    vec2 h = r * 0.5;
    vec2 a = mod(p, r) - h;
    vec2 b = mod(p + h, r) - h;
    vec2 g = dot(a, a) < dot(b, b) ? a : b;
    float edge = 0.5 - hexDist(g);
    return vec4(g, edge, length(p));
}

void main() {
    vec2 uv = gl_TexCoord[0].st;

    vec4 origTexColor = vec4(0.0);
    if (uv.x >= 0.0 && uv.x <= 1.0 && uv.y >= 0.0 && uv.y <= 1.0) {
        vec2 iconGlobalUv = u_iconUvRange.xz + uv * (u_iconUvRange.yw - u_iconUvRange.xz);
        origTexColor = texture2D(texture, iconGlobalUv);
    }

    if (origTexColor.a < 0.05) {
        discard;
    }

    vec2 p = gl_FragCoord.xy * 0.15 + uv * 10.0;
    vec4 hg = hexGrid(p);

    float t = iTime * 3.0;
    float wave = sin(hg.w * 1.5 - t);
    float line = smoothstep(0.02, 0.08, hg.z) * (0.6 + 0.4 * wave);

    vec3 cyan = vec3(0.1, 0.8, 1.0);
    vec3 magenta = vec3(0.9, 0.2, 0.8);
    vec3 shieldColor = mix(cyan, magenta, 0.5 + 0.5 * sin(t + hg.w));

    shieldColor *= line * 1.8;

    vec3 finalColor;
    if (u_hasFbo == 1) {
        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        vec3 background = texture2D(u_screenTexture, clamp(screenUv, vec2(0.001), vec2(0.999))).rgb;
        finalColor = mix(background, shieldColor, origTexColor.a * 0.7);
        finalColor += shieldColor * 0.2;
    } else {
        finalColor = mix(origTexColor.rgb, shieldColor, 0.65);
    }

    gl_FragColor = vec4(finalColor, origTexColor.a);
}
