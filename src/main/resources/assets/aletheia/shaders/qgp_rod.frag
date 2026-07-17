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

    float chaos = iTime * 3.5;
    vec2 coord = gl_FragCoord.xy * 0.12 + uv * 12.0;

    float n1 = hash(floor(coord + chaos));
    float n2 = hash(floor(coord * 1.7 - chaos * 0.8));
    float turbulence = sin(coord.x * 2.3 + chaos) * cos(coord.y * 2.1 - chaos) + n1 * 2.0 - 1.0 + n2;

    float r = 0.5 + 0.5 * sin(turbulence * 3.0 + iTime * 5.0);
    float g = 0.5 + 0.5 * sin(turbulence * 3.0 - iTime * 4.0 + 2.0);
    float b = 0.9 + 0.1 * sin(turbulence * 2.0 + iTime * 7.0);

    float flicker = 0.7 + 0.3 * sin(iTime * 22.0 + turbulence * 4.0);

    vec2 starCoord = gl_FragCoord.xy * 0.45;
    float stars = 0.0;
    vec2 ipos = floor(starCoord);
    vec2 fpos = fract(starCoord);
    float h = hash(ipos);
    if (h > 0.88) {
        float sf = 0.5 + 0.5 * sin(iTime * 18.0 + h * 6.28);
        stars = smoothstep(0.1, 0.0, length(fpos - vec2(0.5))) * sf;
    }

    vec3 qgpColor = vec3(r, g, b) * flicker * 1.6 + vec3(stars);

    vec3 finalColor;
    if (u_hasFbo == 1) {
        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        float warp = 0.04 + 0.03 * sin(iTime * 5.0 + turbulence);
        vec2 refr = (uv - 0.5) * warp;
        vec3 background = texture2D(u_screenTexture, clamp(screenUv + refr, vec2(0.001), vec2(0.999))).rgb;
        finalColor = mix(background, qgpColor, origTexColor.a * 0.7);
    } else {
        finalColor = mix(origTexColor.rgb, qgpColor, 0.65);
    }

    gl_FragColor = vec4(finalColor, origTexColor.a);
}
