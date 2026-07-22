#version 120

uniform sampler2D texture;
uniform sampler2D u_screenTexture;
uniform float iTime;
uniform float u_screenWidth;
uniform float u_screenHeight;
uniform int u_hasFbo;
uniform vec4 u_iconUvRange;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
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

    vec2 p = (uv - vec2(0.5)) * 10.0;
    float dist = length(p);
    float angle = atan(p.y, p.x);

    float t = iTime * 6.0;
    float arc1 = sin(angle * 8.0 + t + noise(p * 2.0) * 6.0);
    float arc2 = cos(angle * 5.0 - t * 1.5 + noise(p * 3.0) * 8.0);
    float arc = abs(arc1 * arc2);

    float spark = pow(arc, 4.0) * 2.5;

    float r = 0.2 + 0.8 * sin(t * 2.0 + dist * 3.0);
    float g = 0.5 + 0.5 * cos(t * 3.0 - dist * 2.0);
    float b = 0.9 + 0.1 * sin(t * 4.0);

    vec3 plasmaColor = vec3(r, g, b) * (0.8 + spark * 1.5);
    plasmaColor += vec3(0.6, 0.9, 1.0) * pow(spark, 2.0);

    vec3 finalColor;
    if (u_hasFbo == 1) {
        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        vec2 refr = vec2(sin(p.y * 5.0 + t), cos(p.x * 5.0 - t)) * 0.015 * spark;
        vec3 background = texture2D(u_screenTexture, clamp(screenUv + refr, vec2(0.001), vec2(0.999))).rgb;
        finalColor = mix(background, plasmaColor, origTexColor.a * 0.75);
    } else {
        finalColor = mix(origTexColor.rgb, plasmaColor, 0.7);
    }

    gl_FragColor = vec4(finalColor, origTexColor.a);
}
